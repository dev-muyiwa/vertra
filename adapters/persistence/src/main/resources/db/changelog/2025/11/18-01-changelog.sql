-- liquibase formatted sql

-- changeset DELL:1763504249456-7
CREATE TABLE otps (id UUID NOT NULL, user_id UUID NOT NULL, token VARCHAR(255) NOT NULL, expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, used_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_otps PRIMARY KEY (id));

-- changeset DELL:1763504249456-8
ALTER TABLE otps ADD CONSTRAINT uc_otps_token UNIQUE (token);

-- changeset DELL:1763504249456-9
CREATE INDEX idx_otp_expires ON otps(expires_at);

-- changeset DELL:1763504249456-10
CREATE UNIQUE INDEX idx_otp_token ON otps(token);

-- changeset DELL:1763504249456-11
CREATE INDEX idx_otp_user ON otps(user_id);

-- changeset DELL:1763504249456-2
CREATE INDEX IF NOT EXISTS idx_user_sessions_refresh_token_hash ON user_sessions(refresh_token_hash);

-- changeset DELL:1763504249456-4
CREATE INDEX IF NOT EXISTS idx_user_sessions_session_token_hash ON user_sessions(session_token_hash);

-- changeset DELL:1763504249456-6
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

