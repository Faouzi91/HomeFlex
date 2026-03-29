-- =============================================================================
-- V4: Vehicle module — images, bookings, condition reports, soft-delete
-- =============================================================================

-- Soft-delete support
ALTER TABLE vehicles.vehicles ADD COLUMN deleted_at TIMESTAMPTZ;

-- ── Vehicle images ──────────────────────────────────────────────────────────
CREATE TABLE vehicles.vehicle_images (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id     UUID         NOT NULL,
    image_url      TEXT         NOT NULL,
    display_order  INTEGER      NOT NULL DEFAULT 0,
    is_primary     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_vimg_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicles.vehicles (id) ON DELETE CASCADE
);

CREATE INDEX idx_vimg_vehicle ON vehicles.vehicle_images (vehicle_id);

-- ── Vehicle bookings (daily-rental calendar) ────────────────────────────────
CREATE TABLE vehicles.vehicle_bookings (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id     UUID          NOT NULL,
    tenant_id      UUID          NOT NULL,
    start_date     DATE          NOT NULL,
    end_date       DATE          NOT NULL,
    total_price    DECIMAL(12,2) NOT NULL,
    currency       VARCHAR(3)    NOT NULL DEFAULT 'XAF',
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    message        TEXT,
    entity_version BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT fk_vbook_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicles.vehicles (id) ON DELETE CASCADE,
    CONSTRAINT fk_vbook_tenant  FOREIGN KEY (tenant_id)
        REFERENCES public.users (id) ON DELETE CASCADE,
    CONSTRAINT chk_vbook_dates  CHECK (end_date >= start_date),
    CONSTRAINT chk_vbook_status CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED'))
);

CREATE INDEX idx_vbook_vehicle ON vehicles.vehicle_bookings (vehicle_id);
CREATE INDEX idx_vbook_tenant  ON vehicles.vehicle_bookings (tenant_id);

-- Partial index for the overlap query — only non-cancelled bookings
CREATE INDEX idx_vbook_overlap
    ON vehicles.vehicle_bookings (vehicle_id, start_date, end_date)
    WHERE status IN ('PENDING', 'CONFIRMED');

-- ── Condition reports (pre-rental inspection) ───────────────────────────────
CREATE TABLE vehicles.condition_reports (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id     UUID         NOT NULL,
    booking_id     UUID,
    reporter_id    UUID         NOT NULL,
    notes          TEXT         NOT NULL,
    mileage_at     INTEGER,
    fuel_level     VARCHAR(20),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_crep_vehicle FOREIGN KEY (vehicle_id)
        REFERENCES vehicles.vehicles (id) ON DELETE CASCADE,
    CONSTRAINT fk_crep_booking FOREIGN KEY (booking_id)
        REFERENCES vehicles.vehicle_bookings (id) ON DELETE SET NULL,
    CONSTRAINT fk_crep_reporter FOREIGN KEY (reporter_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

CREATE INDEX idx_crep_vehicle ON vehicles.condition_reports (vehicle_id);

-- Images attached to a condition report
CREATE TABLE vehicles.condition_report_images (
    report_id  UUID NOT NULL,
    image_url  TEXT NOT NULL,

    CONSTRAINT fk_crimg_report FOREIGN KEY (report_id)
        REFERENCES vehicles.condition_reports (id) ON DELETE CASCADE
);
