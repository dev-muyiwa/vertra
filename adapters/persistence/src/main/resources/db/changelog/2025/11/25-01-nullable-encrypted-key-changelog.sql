-- liquibase formatted sql

-- changeset DELL:1764025827691-1
ALTER TABLE user_devices ALTER COLUMN  encrypted_private_key DROP NOT NULL;

