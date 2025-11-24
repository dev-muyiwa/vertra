-- liquibase formatted sql

-- changeset DELL:1763931796036-9
CREATE TABLE user_devices (id UUID NOT NULL, user_id UUID NOT NULL, device_id VARCHAR(255) NOT NULL, device_name VARCHAR(255) NOT NULL, device_fingerprint VARCHAR(255) NOT NULL, encrypted_private_key TEXT NOT NULL, is_trusted BOOLEAN NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, last_used_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, revoked_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_user_devices PRIMARY KEY (id));

-- changeset DELL:1763931796036-10
ALTER TABLE users ADD account_public_key TEXT;
ALTER TABLE users ADD oauth_id VARCHAR(255);
ALTER TABLE users ADD oauth_provider VARCHAR(20);
ALTER TABLE users ADD profile_picture_url VARCHAR(500);
ALTER TABLE users ADD recovery_encrypted_private_key TEXT;
ALTER TABLE users ADD recovery_salt VARCHAR(255);

-- changeset DELL:1763931796036-11
ALTER TABLE user_sessions ADD device_id UUID;

-- changeset DELL:1763931796036-17
ALTER TABLE user_devices ADD CONSTRAINT uk_user_device UNIQUE (user_id, device_id);

-- changeset DELL:1763931796036-18
CREATE INDEX idx_user_devices_device_id ON user_devices(user_id, device_id);

-- changeset DELL:1763931796036-19
CREATE INDEX idx_user_devices_fingerprint ON user_devices(device_fingerprint);

-- changeset DELL:1763931796036-20
CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);

-- changeset DELL:1763931796036-21
CREATE INDEX idx_users_oauth ON users(oauth_provider, oauth_id);

-- changeset DELL:1763931796036-22
ALTER TABLE users DROP COLUMN has_accepted_terms;
ALTER TABLE users DROP COLUMN password_hash;

-- changeset DELL:1763931796036-1
ALTER TABLE users ALTER COLUMN  first_name DROP NOT NULL;

-- changeset DELL:1763931796036-2
ALTER TABLE users ALTER COLUMN  last_name DROP NOT NULL;

-- changeset DELL:1763931796036-4
CREATE INDEX idx_user_sessions_refresh_token_hash ON user_sessions(refresh_token_hash);

-- changeset DELL:1763931796036-6
CREATE INDEX idx_user_sessions_session_token_hash ON user_sessions(session_token_hash);

-- changeset DELL:1763931796036-8
CREATE INDEX idx_users_email ON users(email);

