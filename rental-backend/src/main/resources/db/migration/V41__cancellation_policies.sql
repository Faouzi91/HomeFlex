-- V41: Cancellation policies (admin-managed reference table).
-- Each policy describes a refund schedule landlords can attach to a listing.

CREATE TABLE cancellation_policies (
    id                      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code                    VARCHAR(40)  UNIQUE NOT NULL,
    name                    VARCHAR(120) NOT NULL,
    description             TEXT,
    -- 0..100. Refund percentage when guest cancels at least
    -- `hours_before_checkin` hours prior to check-in.
    refund_percentage       INTEGER      NOT NULL CHECK (refund_percentage BETWEEN 0 AND 100),
    hours_before_checkin    INTEGER      NOT NULL CHECK (hours_before_checkin >= 0),
    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cancellation_policies_active ON cancellation_policies(is_active);

INSERT INTO cancellation_policies (code, name, description, refund_percentage, hours_before_checkin)
VALUES
    ('FLEXIBLE',  'Flexible',  'Full refund up to 24 hours before check-in.', 100, 24),
    ('MODERATE',  'Moderate',  'Full refund up to 5 days before check-in, 50% after.', 100, 120),
    ('STRICT',    'Strict',    'Full refund only within 48 hours of booking and up to 7 days before check-in.', 100, 168),
    ('NON_REFUNDABLE', 'Non-Refundable', 'No refunds. Lower base price in exchange.', 0, 0);
