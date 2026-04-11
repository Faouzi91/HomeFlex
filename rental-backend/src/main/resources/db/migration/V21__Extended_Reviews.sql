-- V21: Extended Review Details (Category Ratings & Landlord Replies)

ALTER TABLE reviews ADD COLUMN cleanliness_rating INTEGER;
ALTER TABLE reviews ADD COLUMN accuracy_rating INTEGER;
ALTER TABLE reviews ADD COLUMN communication_rating INTEGER;
ALTER TABLE reviews ADD COLUMN location_rating INTEGER;
ALTER TABLE reviews ADD COLUMN checkin_rating INTEGER;
ALTER TABLE reviews ADD COLUMN value_rating INTEGER;
ALTER TABLE reviews ADD COLUMN landlord_reply TEXT;
ALTER TABLE reviews ADD COLUMN replied_at TIMESTAMP;
