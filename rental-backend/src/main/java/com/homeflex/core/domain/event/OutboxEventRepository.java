package com.homeflex.core.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Picks up a batch of unprocessed events that are eligible for delivery.
     * <p>
     * An event is eligible when:
     * <ul>
     *   <li>It has not been processed yet ({@code processed = false})</li>
     *   <li>Its next retry time is null (first attempt) or in the past</li>
     * </ul>
     * <p>
     * {@code FOR UPDATE SKIP LOCKED} ensures that concurrent relay instances
     * (or overlapping scheduled runs) never pick up the same row, providing
     * partition-level parallelism without explicit distributed locks.
     */
    @Query(value = """
            SELECT *
              FROM outbox_events
             WHERE processed = FALSE
               AND (next_retry_at IS NULL OR next_retry_at <= :now)
             ORDER BY created_at ASC
             LIMIT :batchSize
               FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findBatchForRelay(
            @Param("now") Instant now,
            @Param("batchSize") int batchSize
    );
}
