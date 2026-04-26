-- V37: Link bookings to room types for hotel-type properties.
-- room_type_id is NULL for standalone property bookings.
-- number_of_rooms defaults to 1 (one room per booking for non-hotel properties).

ALTER TABLE bookings
    ADD COLUMN IF NOT EXISTS room_type_id   UUID REFERENCES room_types(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS number_of_rooms INT  NOT NULL DEFAULT 1 CHECK (number_of_rooms >= 1);

CREATE INDEX IF NOT EXISTS idx_bookings_room_type ON bookings(room_type_id);
