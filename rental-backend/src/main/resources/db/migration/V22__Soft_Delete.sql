-- V22: Soft Delete Support

ALTER TABLE users ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE bookings ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE reviews ADD COLUMN deleted_at TIMESTAMP;
