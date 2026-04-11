-- V19: Trust Score System

ALTER TABLE users ADD COLUMN trust_score DOUBLE PRECISION DEFAULT 5.0;

-- Optional: Add index on trust_score for filtering/sorting
CREATE INDEX idx_users_trust_score ON users(trust_score);
