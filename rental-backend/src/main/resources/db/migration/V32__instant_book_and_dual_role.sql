-- V32: Instant Book flag on properties
ALTER TABLE properties
    ADD COLUMN IF NOT EXISTS instant_book_enabled BOOLEAN NOT NULL DEFAULT FALSE;
