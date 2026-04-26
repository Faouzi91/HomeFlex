-- V39: Per-unit identity model.
-- Each `RoomType` declares N anonymous units; this table gives every unit
-- its own row with a unit_number, status, and optional floor/notes so that
-- bookings can target a *specific* unit (Booking.com-style with rooms 101,102…
-- rather than a counted bucket).

CREATE TABLE property_units (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_type_id    UUID NOT NULL REFERENCES room_types(id) ON DELETE CASCADE,
    unit_number     VARCHAR(50) NOT NULL,
    floor           INTEGER,
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (room_type_id, unit_number)
);

CREATE INDEX idx_property_units_room_type ON property_units (room_type_id);
CREATE INDEX idx_property_units_status    ON property_units (status);

-- Booking → specific unit (nullable so legacy/aggregate bookings still load).
ALTER TABLE bookings ADD COLUMN unit_id UUID REFERENCES property_units(id);
CREATE INDEX idx_bookings_unit_id ON bookings (unit_id);

-- Backfill: for every existing room_type, create `total_rooms` anonymous units
-- numbered 1..N. This preserves the implicit identity that was already in the
-- count-based model and keeps the per-unit availability lookup honest.
DO $$
DECLARE
    rt RECORD;
    i  INTEGER;
BEGIN
    FOR rt IN SELECT id, total_rooms FROM room_types LOOP
        FOR i IN 1..COALESCE(rt.total_rooms, 1) LOOP
            INSERT INTO property_units (room_type_id, unit_number, status)
            VALUES (rt.id, i::TEXT, 'AVAILABLE')
            ON CONFLICT (room_type_id, unit_number) DO NOTHING;
        END LOOP;
    END LOOP;
END $$;
