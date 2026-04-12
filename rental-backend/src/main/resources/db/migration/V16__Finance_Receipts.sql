-- V16: Automated Finance - Receipts and Invoices

CREATE TABLE receipts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID REFERENCES bookings(id),
    vehicle_booking_id UUID REFERENCES vehicles.vehicle_bookings(id),
    user_id UUID NOT NULL REFERENCES users(id),
    receipt_number VARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'ISSUED',
    receipt_url TEXT,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
