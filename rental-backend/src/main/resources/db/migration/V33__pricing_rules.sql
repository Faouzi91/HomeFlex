-- V33: Dynamic pricing rules per property
CREATE TABLE pricing_rules (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id     UUID        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    rule_type       VARCHAR(20) NOT NULL CHECK (rule_type IN ('WEEKEND','SEASONAL','LONG_STAY')),
    label           VARCHAR(100),
    multiplier      DECIMAL(6,4) NOT NULL CHECK (multiplier > 0),
    min_stay_days   INT,        -- LONG_STAY: applies when stay >= this many days
    start_date      DATE,       -- SEASONAL: inclusive start
    end_date        DATE,       -- SEASONAL: inclusive end
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pricing_rules_property ON pricing_rules(property_id);
