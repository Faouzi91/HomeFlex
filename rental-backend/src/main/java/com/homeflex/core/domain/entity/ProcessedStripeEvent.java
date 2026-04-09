package com.homeflex.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/// One row per Stripe event we have processed. Used to make webhook handling
/// idempotent: Stripe retries until it sees a 2xx, and the same event can be
/// replayed manually from the dashboard. The event_id is the natural primary
/// key — inserting it inside the same transaction as the side effects gives
/// us "at most once" semantics for free.
@Entity
@Table(name = "processed_stripe_events")
@Data
public class ProcessedStripeEvent {

    @Id
    @Column(name = "event_id", length = 255)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @CreationTimestamp
    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;
}
