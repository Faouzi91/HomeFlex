-- V30: Vehicle Booking Workflow Parity
-- Update vehicles.vehicle_bookings to support the same 10-state lifecycle as properties

-- 1. Drop the old CHECK constraint
ALTER TABLE vehicles.vehicle_bookings DROP CONSTRAINT IF EXISTS chk_vbook_status;

-- 2. Add new CHECK constraint with all 10 statuses
ALTER TABLE vehicles.vehicle_bookings ADD CONSTRAINT chk_vbook_status
    CHECK (status IN ('DRAFT', 'PAYMENT_PENDING', 'PAYMENT_FAILED',
                      'PENDING_APPROVAL', 'APPROVED', 'REJECTED',
                      'CANCELLED', 'ACTIVE', 'COMPLETED', 'PENDING_MODIFICATION'));

-- 3. Migrate existing PENDING bookings to PENDING_APPROVAL
UPDATE vehicles.vehicle_bookings SET status = 'PENDING_APPROVAL' WHERE status = 'PENDING';

-- 4. Migrate existing CONFIRMED bookings to APPROVED
UPDATE vehicles.vehicle_bookings SET status = 'APPROVED' WHERE status = 'CONFIRMED';

-- 5. Add new columns for payment integration (parity with property bookings)
ALTER TABLE vehicles.vehicle_bookings ADD COLUMN IF NOT EXISTS payment_status VARCHAR(50);
ALTER TABLE vehicles.vehicle_bookings ADD COLUMN IF NOT EXISTS payment_failure_reason TEXT;
