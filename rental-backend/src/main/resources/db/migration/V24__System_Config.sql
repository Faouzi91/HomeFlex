-- V24: System Configuration

CREATE TABLE system_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description TEXT
);

INSERT INTO system_configs (config_key, config_value, description)
VALUES ('platform_commission_rate', '0.15', 'Default platform commission rate (15%)');
