-- Grant BOOKING_UPDATE to ROLE_TENANT so tenants can initiate and retry payment
-- on their own bookings (POST /bookings/{id}/pay and /retry-payment).
-- ROLE_LANDLORD already had this permission from V28.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_TENANT'
  AND p.name = 'BOOKING_UPDATE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp2
    WHERE rp2.role_id = r.id AND rp2.permission_id = p.id
  );
