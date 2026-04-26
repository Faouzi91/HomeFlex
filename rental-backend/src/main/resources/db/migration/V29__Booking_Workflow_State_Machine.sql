-- 1. Drop the old CHECK constraint and add new one with all 10 statuses
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS bookings_status_check;
ALTER TABLE bookings ADD CONSTRAINT bookings_status_check 
    CHECK (status IN ('DRAFT', 'PAYMENT_PENDING', 'PAYMENT_FAILED', 
                      'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 
                      'CANCELLED', 'ACTIVE', 'COMPLETED', 'PENDING_MODIFICATION'));

-- 2. Migrate existing PENDING bookings to PENDING_APPROVAL
UPDATE bookings SET status = 'PENDING_APPROVAL' WHERE status = 'PENDING';

-- 3. Add new columns to bookings table
ALTER TABLE bookings ADD COLUMN payment_status VARCHAR(50);
ALTER TABLE bookings ADD COLUMN payment_failure_reason TEXT;
ALTER TABLE bookings ADD COLUMN idempotency_key VARCHAR(255) UNIQUE;

-- 4. Create the booking audit log table
CREATE TABLE booking_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id UUID,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_booking_audit_log_booking ON booking_audit_log(booking_id);
CREATE INDEX idx_booking_audit_log_created ON booking_audit_log(created_at);
