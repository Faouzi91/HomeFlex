-- V35: Room type system for hotels, guesthouses, hostels, and resorts.
-- Each property of a hotel-type can define multiple room types (Standard, Deluxe, Suite…).
-- Each room type has a total quantity (e.g. 10 identical rooms available).

CREATE TABLE room_types (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id     UUID        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    bed_type        VARCHAR(20) NOT NULL DEFAULT 'DOUBLE'
                    CHECK (bed_type IN ('SINGLE','DOUBLE','TWIN','QUEEN','KING','BUNK','SOFA')),
    num_beds        INT         NOT NULL DEFAULT 1 CHECK (num_beds >= 1),
    max_occupancy   INT         NOT NULL DEFAULT 2 CHECK (max_occupancy >= 1),
    price_per_night DECIMAL(12,2) NOT NULL,
    currency        VARCHAR(3)  NOT NULL DEFAULT 'XAF',
    total_rooms     INT         NOT NULL DEFAULT 1 CHECK (total_rooms >= 1),
    size_sqm        DECIMAL(8,2),
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE room_type_images (
    id            UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    room_type_id  UUID    NOT NULL REFERENCES room_types(id) ON DELETE CASCADE,
    image_url     TEXT    NOT NULL,
    display_order INT     NOT NULL DEFAULT 0,
    is_primary    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Room types can share the global amenity catalog
CREATE TABLE room_type_amenities (
    room_type_id UUID NOT NULL REFERENCES room_types(id) ON DELETE CASCADE,
    amenity_id   UUID NOT NULL REFERENCES amenities(id) ON DELETE CASCADE,
    PRIMARY KEY (room_type_id, amenity_id)
);

CREATE INDEX idx_room_types_property  ON room_types(property_id);
CREATE INDEX idx_room_types_active    ON room_types(property_id, is_active);
CREATE INDEX idx_room_type_images_rt  ON room_type_images(room_type_id);
