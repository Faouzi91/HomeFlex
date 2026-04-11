-- V23: User Notification Preferences

ALTER TABLE users ADD COLUMN email_notifications_enabled BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN push_notifications_enabled BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN sms_notifications_enabled BOOLEAN DEFAULT TRUE;
