-- V14: Agency White-labeling and Blockchain Lease Integration

-- 1. Agency White-labeling
CREATE TABLE agencies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url TEXT,
    website_url TEXT,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    custom_domain VARCHAR(255) UNIQUE,
    theme_primary_color VARCHAR(10) DEFAULT '#0F172A',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add agency_id to users (Users can belong to an agency)
ALTER TABLE users ADD COLUMN agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL;
ALTER TABLE users ADD COLUMN agency_role VARCHAR(20) CHECK (agency_role IN ('ADMIN', 'AGENT'));

-- Add agency_id to properties (Properties can be managed by an agency)
ALTER TABLE properties ADD COLUMN agency_id UUID REFERENCES agencies(id) ON DELETE SET NULL;

-- 2. Blockchain Lease Integration
ALTER TABLE property_leases ADD COLUMN blockchain_tx_hash VARCHAR(255);
ALTER TABLE property_leases ADD COLUMN on_chain_status VARCHAR(20) DEFAULT 'NOT_MINTED' CHECK (on_chain_status IN ('NOT_MINTED', 'PENDING', 'SUCCESS', 'FAILED'));
ALTER TABLE property_leases ADD COLUMN contract_address VARCHAR(255);
ALTER TABLE property_leases ADD COLUMN token_id VARCHAR(255);
