-- V20: Booking Modification Support

ALTER TABLE bookings ADD COLUMN proposed_start_date DATE;
ALTER TABLE bookings ADD COLUMN proposed_end_date DATE;
ALTER TABLE bookings ADD COLUMN modification_reason TEXT;

-- Update status check constraint (if any) or just assume enum handling in Java
-- If there is a check constraint on status, it might need updating. 
-- Looking at V1__baseline_schema.sql to check.
