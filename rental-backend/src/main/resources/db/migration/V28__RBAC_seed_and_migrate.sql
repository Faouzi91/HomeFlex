-- RBAC: seed permissions, roles, and backfill user_roles from the legacy users.role column.

-- ── 1. Permissions ──────────────────────────────────────────────────────────
INSERT INTO permissions (name, description) VALUES
  ('USER_READ',          'View user profiles'),
  ('USER_WRITE',         'Edit own profile'),
  ('USER_DELETE',        'Delete accounts'),
  ('USER_SUSPEND',       'Suspend or activate accounts'),

  ('PROPERTY_READ',      'View property listings'),
  ('PROPERTY_CREATE',    'Create property listings'),
  ('PROPERTY_UPDATE',    'Edit own property listings'),
  ('PROPERTY_DELETE',    'Delete own property listings'),
  ('PROPERTY_APPROVE',   'Approve or reject listings'),

  ('VEHICLE_READ',       'View vehicle listings'),
  ('VEHICLE_CREATE',     'Create vehicle listings'),
  ('VEHICLE_UPDATE',     'Edit own vehicle listings'),
  ('VEHICLE_DELETE',     'Delete own vehicle listings'),

  ('BOOKING_READ',       'View own bookings'),
  ('BOOKING_CREATE',     'Create bookings'),
  ('BOOKING_UPDATE',     'Modify bookings'),
  ('BOOKING_CANCEL',     'Cancel bookings'),
  ('BOOKING_APPROVE',    'Approve or reject booking requests'),

  ('LEASE_READ',         'View leases'),
  ('LEASE_CREATE',       'Create lease contracts'),
  ('LEASE_SIGN',         'Digitally sign leases'),

  ('DISPUTE_READ',       'View own disputes'),
  ('DISPUTE_CREATE',     'Open disputes'),
  ('DISPUTE_RESOLVE',    'Resolve disputes'),

  ('REVIEW_READ',        'View reviews'),
  ('REVIEW_CREATE',      'Write reviews'),
  ('REVIEW_MODERATE',    'Remove inappropriate reviews'),

  ('PAYMENT_READ',       'View own payment history'),
  ('PAYMENT_PROCESS',    'Process payments'),
  ('PAYMENT_REFUND',     'Issue refunds'),

  ('INSURANCE_READ',     'View insurance plans'),
  ('INSURANCE_PURCHASE', 'Purchase insurance policies'),
  ('INSURANCE_MANAGE',   'Manage insurance products'),

  ('KYC_SUBMIT',         'Submit KYC documents'),
  ('KYC_READ',           'View own KYC status'),
  ('KYC_APPROVE',        'Approve or reject KYC submissions'),

  ('REPORT_CREATE',      'Report content'),
  ('REPORT_RESOLVE',     'Resolve reported content'),

  ('MAINTENANCE_READ',   'View maintenance requests'),
  ('MAINTENANCE_CREATE', 'Submit maintenance requests'),
  ('MAINTENANCE_UPDATE', 'Update maintenance status'),

  ('ACTUATOR_READ',      'Read monitoring actuator endpoints'),
  ('ADMIN_DASHBOARD',    'Access admin dashboard'),
  ('ADMIN_USERS',        'Manage all users'),
  ('ADMIN_CONTENT',      'Manage all listings');

-- ── 2. Roles ────────────────────────────────────────────────────────────────
INSERT INTO roles (name, description) VALUES
  ('ROLE_TENANT',     'Standard tenant — can book, lease, and open disputes'),
  ('ROLE_LANDLORD',   'Property and vehicle owner — can list assets and approve bookings'),
  ('ROLE_ADMIN',      'Full platform administrator'),
  ('ROLE_MONITORING', 'Read-only access for CI/CD and Prometheus scraping');

-- ── 3. ROLE_TENANT permissions ──────────────────────────────────────────────
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_TENANT'
  AND p.name IN (
    'USER_READ', 'USER_WRITE',
    'PROPERTY_READ', 'VEHICLE_READ',
    'BOOKING_READ', 'BOOKING_CREATE', 'BOOKING_CANCEL',
    'LEASE_READ', 'LEASE_SIGN',
    'DISPUTE_READ', 'DISPUTE_CREATE',
    'REVIEW_READ', 'REVIEW_CREATE',
    'PAYMENT_READ', 'PAYMENT_PROCESS',
    'INSURANCE_READ', 'INSURANCE_PURCHASE',
    'KYC_SUBMIT', 'KYC_READ',
    'REPORT_CREATE',
    'MAINTENANCE_READ', 'MAINTENANCE_CREATE'
  );

-- ── 4. ROLE_LANDLORD permissions ────────────────────────────────────────────
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_LANDLORD'
  AND p.name IN (
    'USER_READ', 'USER_WRITE',
    'PROPERTY_READ', 'PROPERTY_CREATE', 'PROPERTY_UPDATE', 'PROPERTY_DELETE',
    'VEHICLE_READ',  'VEHICLE_CREATE',  'VEHICLE_UPDATE',  'VEHICLE_DELETE',
    'BOOKING_READ', 'BOOKING_CREATE', 'BOOKING_CANCEL', 'BOOKING_UPDATE', 'BOOKING_APPROVE',
    'LEASE_READ', 'LEASE_CREATE', 'LEASE_SIGN',
    'DISPUTE_READ', 'DISPUTE_CREATE',
    'REVIEW_READ', 'REVIEW_CREATE',
    'PAYMENT_READ', 'PAYMENT_PROCESS',
    'INSURANCE_READ', 'INSURANCE_PURCHASE', 'INSURANCE_MANAGE',
    'KYC_SUBMIT', 'KYC_READ',
    'REPORT_CREATE',
    'MAINTENANCE_READ', 'MAINTENANCE_CREATE', 'MAINTENANCE_UPDATE'
  );

-- ── 5. ROLE_ADMIN gets all permissions ──────────────────────────────────────
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

-- ── 6. ROLE_MONITORING permissions ──────────────────────────────────────────
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_MONITORING' AND p.name = 'ACTUATOR_READ';

-- ── 7. Backfill user_roles from legacy users.role ───────────────────────────
-- Only applies to rows already in the users table at migration time.
-- Users created after this migration are handled by DataInitializer/AuthService.
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_' || u.role
WHERE u.deleted_at IS NULL;
