-- V15: Insurance Marketplace Implementation

CREATE TABLE insurance_providers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    logo_url TEXT,
    website_url TEXT,
    description TEXT,
    contact_email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE insurance_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider_id UUID NOT NULL REFERENCES insurance_providers(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'TENANT', 'LANDLORD', 'VEHICLE'
    description TEXT,
    coverage_details JSONB,
    daily_premium DECIMAL(10, 2) NOT NULL,
    max_coverage_amount DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE insurance_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_id UUID NOT NULL REFERENCES insurance_plans(id),
    user_id UUID NOT NULL REFERENCES users(id),
    booking_id UUID REFERENCES bookings(id),
    vehicle_booking_id UUID REFERENCES vehicle_bookings(id),
    policy_number VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 'PENDING', 'ACTIVE', 'EXPIRED', 'CANCELLED'
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_premium DECIMAL(10, 2) NOT NULL,
    certificate_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed some mock insurance data
INSERT INTO insurance_providers (name, description, logo_url) 
VALUES ('HomeFlex Guard', 'Official HomeFlex insurance partner providing seamless coverage.', 'https://assets.homeflex.com/insurance/guard-logo.png');

INSERT INTO insurance_plans (provider_id, name, type, description, daily_premium, max_coverage_amount, coverage_details)
SELECT id, 'Essential Tenant Protection', 'TENANT', 'Covers accidental damages and personal liability during your stay.', 2.50, 50000.00, 
'{"items": ["Accidental damage", "Personal liability", "Medical expenses"], "deductible": 100}'
FROM insurance_providers WHERE name = 'HomeFlex Guard';

INSERT INTO insurance_plans (provider_id, name, type, description, daily_premium, max_coverage_amount, coverage_details)
SELECT id, 'Comprehensive Landlord Shield', 'LANDLORD', 'Total protection against tenant damages, loss of rent, and legal expenses.', 4.00, 250000.00,
'{"items": ["Severe property damage", "Loss of rent", "Legal defense"], "deductible": 500}'
FROM insurance_providers WHERE name = 'HomeFlex Guard';
