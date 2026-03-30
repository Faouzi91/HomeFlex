-- Stripe Connect: landlord connected accounts + escrow tracking

ALTER TABLE users ADD COLUMN stripe_account_id VARCHAR(255);

-- Property bookings: add payment tracking columns
ALTER TABLE bookings ADD COLUMN total_price       DECIMAL(12,2);
ALTER TABLE bookings ADD COLUMN platform_fee      DECIMAL(12,2);
ALTER TABLE bookings ADD COLUMN stripe_payment_intent_id VARCHAR(255);
ALTER TABLE bookings ADD COLUMN escrow_released_at TIMESTAMPTZ;

-- Vehicle bookings: add payment tracking columns (total_price already exists)
ALTER TABLE vehicles.vehicle_bookings ADD COLUMN platform_fee              DECIMAL(12,2);
ALTER TABLE vehicles.vehicle_bookings ADD COLUMN stripe_payment_intent_id  VARCHAR(255);
ALTER TABLE vehicles.vehicle_bookings ADD COLUMN escrow_released_at        TIMESTAMPTZ;
