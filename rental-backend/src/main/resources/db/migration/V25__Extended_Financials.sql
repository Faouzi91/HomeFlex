-- V25: Extended Financials and Cancellation Policies

ALTER TABLE bookings ADD COLUMN cleaning_fee DECIMAL(12, 2) DEFAULT 0;
ALTER TABLE bookings ADD COLUMN tax_amount DECIMAL(12, 2) DEFAULT 0;

ALTER TABLE properties ADD COLUMN cancellation_policy VARCHAR(20) DEFAULT 'FLEXIBLE' CHECK (cancellation_policy IN ('FLEXIBLE', 'MODERATE', 'STRICT'));
ALTER TABLE properties ADD COLUMN cleaning_fee DECIMAL(12, 2) DEFAULT 0;
ALTER TABLE properties ADD COLUMN security_deposit DECIMAL(12, 2) DEFAULT 0;
