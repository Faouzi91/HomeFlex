-- Per-listing availability calendar.
--
-- Sparse "exception" model: only blocked or booked dates are stored. Any date
-- with no row is considered available (subject to the listing's
-- `available_from` field). This is the same shape Airbnb / Booking use and
-- avoids accumulating millions of dead "available" rows as the catalog grows.
--
-- Status values:
--   BLOCKED — host manually blocked the date (off-market, maintenance, etc.)
--   BOOKED  — date is reserved by an active booking
CREATE TABLE property_availability (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id  UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    date         DATE NOT NULL,
    status       VARCHAR(20) NOT NULL,
    booking_id   UUID REFERENCES bookings(id) ON DELETE CASCADE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT property_availability_status_chk
        CHECK (status IN ('BLOCKED', 'BOOKED')),
    -- A given (property, date) pair can only be blocked once. This is what
    -- guarantees no double-booking: two concurrent bookings inserting the
    -- same date will collide on this unique constraint and one will fail.
    CONSTRAINT property_availability_unique UNIQUE (property_id, date)
);

CREATE INDEX idx_property_availability_property_date
    ON property_availability (property_id, date);
CREATE INDEX idx_property_availability_booking
    ON property_availability (booking_id);
