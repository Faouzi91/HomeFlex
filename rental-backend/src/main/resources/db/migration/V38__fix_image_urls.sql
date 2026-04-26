-- V38: Rewrite MinIO internal URLs to Nginx-proxied relative paths.
-- Images were stored as http://minio:9000/rental-app-media/<key>
-- Nginx now proxies /uploads/<key> → http://minio:9000/rental-app-media/<key>

UPDATE property_images
SET image_url = REPLACE(image_url, 'http://minio:9000/rental-app-media/', '/uploads/')
WHERE image_url LIKE 'http://minio:9000%';

UPDATE vehicles.vehicle_images
SET image_url = REPLACE(image_url, 'http://minio:9000/rental-app-media/', '/uploads/')
WHERE image_url LIKE 'http://minio:9000%';

UPDATE room_type_images
SET image_url = REPLACE(image_url, 'http://minio:9000/rental-app-media/', '/uploads/')
WHERE image_url LIKE 'http://minio:9000%';

UPDATE users
SET profile_picture_url = REPLACE(profile_picture_url, 'http://minio:9000/rental-app-media/', '/uploads/')
WHERE profile_picture_url LIKE 'http://minio:9000%';
