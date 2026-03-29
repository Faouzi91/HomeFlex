-- =============================================================================
-- V3: Vehicle Module — separate schema for package-by-feature isolation
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS vehicles;

CREATE TABLE vehicles.vehicles (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id        UUID            NOT NULL,
    brand           VARCHAR(100)    NOT NULL,
    model           VARCHAR(100)    NOT NULL,
    year            INTEGER         NOT NULL,
    transmission    VARCHAR(20)     NOT NULL DEFAULT 'MANUAL',
    fuel_type       VARCHAR(20)     NOT NULL DEFAULT 'GASOLINE',
    daily_price     DECIMAL(12, 2)  NOT NULL,
    currency        VARCHAR(3)      NOT NULL DEFAULT 'XAF',
    status          VARCHAR(20)     NOT NULL DEFAULT 'AVAILABLE',
    description     TEXT,
    mileage         INTEGER,
    seats           INTEGER,
    color           VARCHAR(50),
    license_plate   VARCHAR(20),
    pickup_city     VARCHAR(100),
    pickup_address  TEXT,
    view_count      INTEGER         NOT NULL DEFAULT 0,
    entity_version  BIGINT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    -- FK to public.users — cross-schema reference to the core module
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id)
        REFERENCES public.users (id) ON DELETE CASCADE,

    -- Domain constraints
    CONSTRAINT chk_vehicle_year       CHECK (year BETWEEN 1900 AND 2100),
    CONSTRAINT chk_vehicle_price      CHECK (daily_price > 0),
    CONSTRAINT chk_vehicle_status     CHECK (status IN ('AVAILABLE', 'BOOKED', 'MAINTENANCE')),
    CONSTRAINT chk_vehicle_trans      CHECK (transmission IN ('MANUAL', 'AUTO')),
    CONSTRAINT chk_vehicle_fuel       CHECK (fuel_type IN ('GASOLINE', 'DIESEL', 'ELECTRIC', 'HYBRID'))
);

-- Covering indexes for the search/filter query
CREATE INDEX idx_vehicles_status      ON vehicles.vehicles (status);
CREATE INDEX idx_vehicles_owner       ON vehicles.vehicles (owner_id);
CREATE INDEX idx_vehicles_brand_model ON vehicles.vehicles (brand, model);
CREATE INDEX idx_vehicles_price       ON vehicles.vehicles (daily_price);
CREATE INDEX idx_vehicles_city        ON vehicles.vehicles (pickup_city);
CREATE INDEX idx_vehicles_created     ON vehicles.vehicles (created_at DESC);
