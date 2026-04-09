-- Stripe webhook idempotency.
--
-- Stripe retries webhook deliveries until it sees a 2xx, and the same event
-- can also be replayed by a developer from the dashboard. Without dedup,
-- payment_intent.succeeded would re-create bookings / notifications on every
-- replay. We dedup by Stripe's own immutable event id.
CREATE TABLE processed_stripe_events (
    event_id    VARCHAR(255) PRIMARY KEY,
    event_type  VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_processed_stripe_events_processed_at
    ON processed_stripe_events (processed_at);
