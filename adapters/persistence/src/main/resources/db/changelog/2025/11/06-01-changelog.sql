-- liquibase formatted sql

-- changeset DELL:1762419726535-3
CREATE TABLE user_sessions (id UUID NOT NULL, user_id UUID NOT NULL, session_token_hash VARCHAR(255) NOT NULL, refresh_token_hash VARCHAR(255), ip_address VARCHAR(45), user_agent TEXT, device_fingerprint VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, last_activity_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, revoked_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_user_sessions PRIMARY KEY (id));

-- changeset DELL:1762419726535-4
ALTER TABLE users ADD failed_login_attempts INTEGER DEFAULT 0;
ALTER TABLE users ADD locked_until TIMESTAMP WITHOUT TIME ZONE;

-- changeset DELL:1762419726535-5
ALTER TABLE users ALTER COLUMN  failed_login_attempts SET NOT NULL;

-- changeset DELL:1762419726535-7
ALTER TABLE user_sessions ADD CONSTRAINT uc_user_sessions_refresh_token_hash UNIQUE (refresh_token_hash);

-- changeset DELL:1762419726535-8
ALTER TABLE user_sessions ADD CONSTRAINT uc_user_sessions_session_token_hash UNIQUE (session_token_hash);

-- changeset DELL:1762419726535-9
CREATE INDEX idx_user_sessions_active ON user_sessions(user_id, revoked_at);

-- changeset DELL:1762419726535-10
CREATE INDEX idx_user_sessions_refresh_token_hash ON user_sessions(refresh_token_hash);

-- changeset DELL:1762419726535-11
CREATE INDEX idx_user_sessions_session_token_hash ON user_sessions(session_token_hash);

-- changeset DELL:1762419726535-12
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id, created_at);

-- changeset DELL:1762419726535-2
CREATE INDEX idx_users_email ON users(email);

