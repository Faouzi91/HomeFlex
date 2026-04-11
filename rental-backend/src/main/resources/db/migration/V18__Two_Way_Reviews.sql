-- V18: Two-Way Reviews Implementation

-- Add type and target_user_id to reviews table
ALTER TABLE reviews ADD COLUMN type VARCHAR(20) DEFAULT 'PROPERTY' CHECK (type IN ('PROPERTY', 'TENANT'));
ALTER TABLE reviews ADD COLUMN target_user_id UUID REFERENCES users(id) ON DELETE CASCADE;

-- Rename property_id to allow it to be nullable (for tenant reviews)
ALTER TABLE reviews ALTER COLUMN property_id DROP NOT NULL;

-- Add index for performance
CREATE INDEX idx_reviews_target_user ON reviews(target_user_id);
CREATE INDEX idx_reviews_type ON reviews(type);
