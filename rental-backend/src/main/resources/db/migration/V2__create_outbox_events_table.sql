-- Outbox Events table for Transactional Outbox Pattern
-- Used by OutboxRelayService to publish domain events to RabbitMQ
CREATE TABLE outbox_events (
    id             UUID         PRIMARY KEY,
    aggregate_type VARCHAR(120) NOT NULL,
    aggregate_id   UUID         NOT NULL,
    event_type     VARCHAR(120) NOT NULL,
    payload        TEXT         NOT NULL,
    processed      BOOLEAN      NOT NULL DEFAULT FALSE,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    next_retry_at  TIMESTAMPTZ,
    last_error     TEXT,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Covering index for the relay query: unprocessed events eligible for retry,
-- ordered by creation time. Supports FOR UPDATE SKIP LOCKED efficiently.
CREATE INDEX idx_outbox_unprocessed
    ON outbox_events (created_at ASC)
    WHERE processed = FALSE;
