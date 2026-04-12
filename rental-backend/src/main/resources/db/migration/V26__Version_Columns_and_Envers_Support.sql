-- =============================================================================
-- V26: Add @Version columns and Hibernate Envers audit infrastructure
-- =============================================================================

-- 1. Add optimistic locking columns missing from baseline migrations
ALTER TABLE users ADD COLUMN entity_version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE bookings ADD COLUMN entity_version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE properties ADD COLUMN entity_version BIGINT NOT NULL DEFAULT 0;

-- 2. Soft-delete for properties (V22 missed this table)
ALTER TABLE properties ADD COLUMN deleted_at TIMESTAMP;

-- 3. Envers revision tracking table + sequence
CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revinfo (
    rev        INTEGER PRIMARY KEY DEFAULT nextval('revinfo_seq'),
    revtstmp   BIGINT
);

-- 3. Booking audit table (@Audited(withModifiedFlag = true))
--    Every audited column gets a companion <col>_mod boolean flag.
CREATE TABLE bookings_aud (
    id                       UUID      NOT NULL,
    rev                      INTEGER   NOT NULL REFERENCES revinfo(rev),
    revtype                  SMALLINT,

    property_id              UUID,
    property_mod             BOOLEAN,
    tenant_id                UUID,
    tenant_mod               BOOLEAN,
    booking_type             VARCHAR(20),
    booking_type_mod         BOOLEAN,
    requested_date           TIMESTAMP,
    requested_date_mod       BOOLEAN,
    start_date               DATE,
    start_date_mod           BOOLEAN,
    end_date                 DATE,
    end_date_mod             BOOLEAN,
    status                   VARCHAR(20),
    status_mod               BOOLEAN,
    message                  TEXT,
    message_mod              BOOLEAN,
    number_of_occupants      INTEGER,
    number_of_occupants_mod  BOOLEAN,
    total_price              DECIMAL(12, 2),
    total_price_mod          BOOLEAN,
    platform_fee             DECIMAL(12, 2),
    platform_fee_mod         BOOLEAN,
    cleaning_fee             DECIMAL(12, 2),
    cleaning_fee_mod         BOOLEAN,
    tax_amount               DECIMAL(12, 2),
    tax_amount_mod           BOOLEAN,
    stripe_payment_intent_id VARCHAR(255),
    stripe_payment_intent_id_mod BOOLEAN,
    payment_confirmed_at     TIMESTAMP,
    payment_confirmed_at_mod BOOLEAN,
    escrow_released_at       TIMESTAMP,
    escrow_released_at_mod   BOOLEAN,
    landlord_response        TEXT,
    landlord_response_mod    BOOLEAN,
    responded_at             TIMESTAMP,
    responded_at_mod         BOOLEAN,
    proposed_start_date      DATE,
    proposed_start_date_mod  BOOLEAN,
    proposed_end_date        DATE,
    proposed_end_date_mod    BOOLEAN,
    modification_reason      TEXT,
    modification_reason_mod  BOOLEAN,
    created_at               TIMESTAMP,
    created_at_mod           BOOLEAN,
    updated_at               TIMESTAMP,
    updated_at_mod           BOOLEAN,
    deleted_at               TIMESTAMP,
    deleted_at_mod           BOOLEAN,
    entity_version           BIGINT,
    entity_version_mod       BOOLEAN,

    PRIMARY KEY (id, rev)
);

CREATE INDEX idx_bookings_aud_rev ON bookings_aud(rev);
