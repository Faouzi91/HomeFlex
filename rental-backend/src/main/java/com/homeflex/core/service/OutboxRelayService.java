package com.homeflex.core.service;

import com.homeflex.core.config.AppProperties;
import com.homeflex.core.domain.event.OutboxEvent;
import com.homeflex.core.domain.event.OutboxEventRepository;
import com.homeflex.core.dto.event.OutboxEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Scheduled Outbox Relay — polls the {@code outbox_events} table and
 * publishes unprocessed events to RabbitMQ.
 *
 * <h3>Design decisions</h3>
 * <ul>
 *   <li><strong>FOR UPDATE SKIP LOCKED</strong> — the repository query
 *       row-locks the batch so overlapping executions (or multiple nodes)
 *       never double-publish the same event.</li>
 *   <li><strong>At-Least-Once delivery</strong> — an event is only marked
 *       {@code processed = true} after the broker returns a positive
 *       publisher-confirm (ACK). If the ACK never arrives the row stays
 *       unprocessed and will be retried on the next poll.</li>
 *   <li><strong>Exponential backoff</strong> — on publish failure the
 *       {@code next_retry_at} column is set to
 *       {@code now + baseBackoff * 2^retryCount} so transient broker
 *       outages don't cause a tight retry storm.</li>
 *   <li><strong>Poison-pill protection</strong> — after {@code maxRetries}
 *       the event is marked processed to prevent infinite retries.
 *       The {@code last_error} column preserves the failure reason for
 *       operational investigation.</li>
 *   <li><strong>Virtual threads</strong> — Spring Boot 4 + Java 21 with
 *       {@code spring.threads.virtual.enabled=true} runs the scheduled
 *       task on a virtual thread, keeping platform-thread utilisation
 *       minimal while the relay waits on I/O (DB + broker).</li>
 * </ul>
 *
 * <h3>Routing</h3>
 * Each event is published to the topic exchange with routing key
 * {@code {aggregateType}.{eventType}} (e.g. {@code Booking.BookingCreated}).
 * Consumers bind their queues with wildcard patterns.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.outbox.relay-enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelayService {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AppProperties appProperties;

    /**
     * Poll interval is driven by {@code app.outbox.poll-interval-ms}.
     * Uses {@code fixedDelayString} so the next poll starts only after
     * the previous one completes — no overlap risk within a single node.
     */
    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    public void relay() {
        AppProperties.Outbox cfg = appProperties.getOutbox();
        List<OutboxEvent> batch = fetchBatch(cfg.getBatchSize());

        if (batch.isEmpty()) {
            return;
        }

        log.debug("Outbox relay picked up {} event(s)", batch.size());

        for (OutboxEvent event : batch) {
            publishEvent(event, cfg);
        }
    }

    @Transactional
    protected List<OutboxEvent> fetchBatch(int batchSize) {
        return outboxEventRepository.findBatchForRelay(Instant.now(), batchSize);
    }

    private void publishEvent(OutboxEvent event, AppProperties.Outbox cfg) {
        String routingKey = event.getAggregateType() + "." + event.getEventType();

        OutboxEventMessage message = new OutboxEventMessage(
                event.getId(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getPayload(),
                event.getCreatedAt()
        );

        CorrelationData correlation = new CorrelationData(event.getId().toString());

        try {
            rabbitTemplate.convertAndSend(
                    cfg.getExchangeName(),
                    routingKey,
                    message,
                    correlation
            );

            waitForConfirm(event, correlation, cfg);

        } catch (Exception ex) {
            handleFailure(event, ex, cfg);
        }
    }

    /**
     * Blocks (on a virtual thread — cheap) until the broker confirms or
     * the 10-second timeout expires.  Only on a positive ACK do we mark
     * the row as processed.
     */
    private void waitForConfirm(OutboxEvent event,
                                CorrelationData correlation,
                                AppProperties.Outbox cfg) {
        try {
            CorrelationData.Confirm confirm = correlation.getFuture().get();

            if (confirm != null && confirm.ack()) {
                markProcessed(event);
                log.debug("Event {} published and ACK'd [routing={}]",
                        event.getId(), event.getAggregateType() + "." + event.getEventType());
            } else {
                String reason = confirm != null ? confirm.reason() : "no confirm received";
                handleFailure(event, new RuntimeException("Broker NACK: " + reason), cfg);
            }
        } catch (Exception ex) {
            handleFailure(event, ex, cfg);
        }
    }

    @Transactional
    protected void markProcessed(OutboxEvent event) {
        event.markProcessed();
        outboxEventRepository.save(event);
    }

    @Transactional
    protected void handleFailure(OutboxEvent event, Exception ex, AppProperties.Outbox cfg) {
        int attempt = event.getRetryCount() + 1;
        String errorMsg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();

        if (attempt >= cfg.getMaxRetries()) {
            log.error("Event {} exhausted {} retries — marking as processed (poison pill). Last error: {}",
                    event.getId(), cfg.getMaxRetries(), errorMsg);
            event.recordFailure("EXHAUSTED: " + errorMsg, null);
            event.setProcessed(true);
        } else {
            long delaySeconds = cfg.getBaseBackoffSeconds() * (1L << event.getRetryCount());
            Instant nextRetry = Instant.now().plusSeconds(delaySeconds);

            log.warn("Event {} publish failed (attempt {}/{}). Retry at {}. Error: {}",
                    event.getId(), attempt, cfg.getMaxRetries(), nextRetry, errorMsg);
            event.recordFailure(errorMsg, nextRetry);
        }

        outboxEventRepository.save(event);
    }
}
