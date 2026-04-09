CREATE TABLE property_leases (
    id UUID PRIMARY KEY,
    property_id UUID NOT NULL REFERENCES properties(id),
    booking_id UUID REFERENCES bookings(id),
    landlord_id UUID NOT NULL REFERENCES users(id),
    tenant_id UUID REFERENCES users(id),
    lease_url TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    signed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_property_leases_property ON property_leases(property_id);
CREATE INDEX idx_property_leases_booking ON property_leases(booking_id);
CREATE INDEX idx_property_leases_landlord ON property_leases(landlord_id);
CREATE INDEX idx_property_leases_tenant ON property_leases(tenant_id);
