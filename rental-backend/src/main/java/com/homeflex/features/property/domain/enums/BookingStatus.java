package com.homeflex.features.property.domain.enums;

/**
 * Production-grade booking lifecycle statuses.
 *
 * State machine transitions are enforced by
 * {@link com.homeflex.features.property.domain.BookingStateMachine}.
 *
 * <pre>
 * DRAFT ──► PAYMENT_PENDING ──► PENDING_APPROVAL ──► APPROVED ──► ACTIVE ──► COMPLETED
 *   │              │                    │                 │
 *   └─► CANCELLED  ├─► PAYMENT_FAILED   ├─► REJECTED      ├─► CANCELLED
 *                  │        │            └─► CANCELLED     └─► PENDING_MODIFICATION
 *                  │        └─► PAYMENT_PENDING (retry)
 *                  └─► CANCELLED
 * </pre>
 *
 * VIEWING bookings skip payment: DRAFT → PENDING_APPROVAL directly.
 */
public enum BookingStatus {
    DRAFT,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    CANCELLED,
    ACTIVE,
    COMPLETED,
    PENDING_MODIFICATION
}
