package com.homeflex.core.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable message published to RabbitMQ from the outbox relay.
 * <p>
 * Consumers deserialize this record to learn which aggregate changed,
 * what happened, and the full JSON payload of the domain event.
 *
 * @param eventId       unique outbox row identifier (idempotency key for consumers)
 * @param aggregateType bounded-context aggregate, e.g. "Booking", "Property"
 * @param aggregateId   the ID of the aggregate instance that produced the event
 * @param eventType     domain event name, e.g. "BookingCreated", "PropertyApproved"
 * @param payload       the serialized domain event body (JSON string)
 * @param occurredAt    when the event was originally enqueued
 */
public record OutboxEventMessage(
        UUID eventId,
        String aggregateType,
        UUID aggregateId,
        String eventType,
        String payload,
        LocalDateTime occurredAt
) {
}
