-- V40: Seed additional system configuration keys.
-- These mirror business constants previously hard-coded throughout services.
-- Admins can adjust them at runtime via /admin/configs.

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('booking_min_advance_hours', '24', 'Minimum hours of advance notice required to book a property')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('booking_max_advance_days', '365', 'Maximum number of days in advance a booking can be made')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('cancellation_grace_hours', '24', 'Hours after booking creation during which a free cancellation is allowed')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('payout_release_delay_days', '1', 'Days after check-in before escrowed funds are released to the landlord')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('platform_currency_default', 'USD', 'Default ISO-4217 currency code for new listings')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('kyc_required_for_publish', 'true', 'Whether landlords must complete KYC verification before publishing a listing')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('property_max_images', '20', 'Maximum number of images allowed on a single property listing')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('dispute_response_window_hours', '72', 'Hours a landlord has to respond to a tenant dispute before auto-escalation')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('rate_limit_login_per_minute', '5', 'Maximum login attempts per minute per IP address')
ON CONFLICT (config_key) DO NOTHING;

INSERT INTO system_configs (config_key, config_value, description)
VALUES
    ('search_index_batch_size', '100', 'Batch size for outbox-driven Elasticsearch reindex jobs')
ON CONFLICT (config_key) DO NOTHING;
