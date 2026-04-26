-- V36: Count-based room inventory for hotel-type properties.
-- One row per (room_type, date) storing how many rooms are booked on that day.
-- available = room_types.total_rooms - rooms_booked
-- Absence of a row means 0 rooms are booked (full availability).

CREATE TABLE room_inventory (
    room_type_id UUID        NOT NULL REFERENCES room_types(id) ON DELETE CASCADE,
    date         DATE        NOT NULL,
    rooms_booked INT         NOT NULL DEFAULT 0 CHECK (rooms_booked >= 0),
    PRIMARY KEY (room_type_id, date)
);

CREATE INDEX idx_room_inventory_date ON room_inventory(date);
