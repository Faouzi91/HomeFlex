-- V34: Booking.com-inspired property model enhancement
-- Adds structured policies, check-in/out times, star rating, DRAFT/SUSPENDED status,
-- and new property/listing types for hotels and hostels.

-- 1. Extend property_type enum
ALTER TABLE properties
    DROP CONSTRAINT IF EXISTS properties_property_type_check;
ALTER TABLE properties
    ADD CONSTRAINT properties_property_type_check
    CHECK (property_type IN (
        'APARTMENT','HOUSE','STUDIO','VILLA','ROOM','OFFICE','LAND',
        'HOTEL','GUESTHOUSE','HOSTEL','RESORT'
    ));

-- 2. Extend listing_type enum
ALTER TABLE properties
    DROP CONSTRAINT IF EXISTS properties_listing_type_check;
ALTER TABLE properties
    ADD CONSTRAINT properties_listing_type_check
    CHECK (listing_type IN ('RENT','SALE','SHORT_TERM','NIGHTLY'));

-- 3. Extend status enum
ALTER TABLE properties
    DROP CONSTRAINT IF EXISTS properties_status_check;
ALTER TABLE properties
    ADD CONSTRAINT properties_status_check
    CHECK (status IN ('DRAFT','PENDING','APPROVED','REJECTED','INACTIVE','SUSPENDED'));

-- 4. Add policy + policy fields
ALTER TABLE properties
    ADD COLUMN IF NOT EXISTS check_in_time        TIME,
    ADD COLUMN IF NOT EXISTS check_out_time       TIME,
    ADD COLUMN IF NOT EXISTS star_rating          SMALLINT CHECK (star_rating BETWEEN 1 AND 5),
    ADD COLUMN IF NOT EXISTS pets_allowed         BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS smoking_allowed      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS children_allowed     BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS min_stay_nights      INT     NOT NULL DEFAULT 1 CHECK (min_stay_nights >= 1),
    ADD COLUMN IF NOT EXISTS max_stay_nights      INT              CHECK (max_stay_nights IS NULL OR max_stay_nights >= 1),
    ADD COLUMN IF NOT EXISTS house_rules          TEXT,
    ADD COLUMN IF NOT EXISTS rejection_reason     TEXT,
    ADD COLUMN IF NOT EXISTS submitted_at         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS approved_at          TIMESTAMP;

-- 5. Indexes for new status values
CREATE INDEX IF NOT EXISTS idx_properties_status_draft     ON properties(status) WHERE status = 'DRAFT';
CREATE INDEX IF NOT EXISTS idx_properties_status_suspended ON properties(status) WHERE status = 'SUSPENDED';
CREATE INDEX IF NOT EXISTS idx_properties_star_rating      ON properties(star_rating);
