-- liquibase formatted sql

-- changeset DELL:1763099652987-1
CREATE TABLE user_sessions (id UUID NOT NULL, user_id UUID NOT NULL, session_token_hash VARCHAR(255) NOT NULL, refresh_token_hash VARCHAR(255), ip_address VARCHAR(45), user_agent TEXT, device_fingerprint VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, last_activity_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, revoked_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_user_sessions PRIMARY KEY (id));

-- changeset DELL:1763099652987-2
CREATE TABLE users (id UUID NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, password_hash VARCHAR(255) NOT NULL, has_accepted_terms BOOLEAN DEFAULT TRUE NOT NULL, email_verified_at TIMESTAMP WITHOUT TIME ZONE, last_login_at TIMESTAMP WITHOUT TIME ZONE, locked_until TIMESTAMP WITHOUT TIME ZONE, failed_login_attempts INTEGER DEFAULT 0 NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, deleted_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_users PRIMARY KEY (id));

-- changeset DELL:1763099652987-3
ALTER TABLE user_sessions ADD CONSTRAINT uc_user_sessions_refresh_token_hash UNIQUE (refresh_token_hash);

-- changeset DELL:1763099652987-4
ALTER TABLE user_sessions ADD CONSTRAINT uc_user_sessions_session_token_hash UNIQUE (session_token_hash);

-- changeset DELL:1763099652987-5
ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email);

-- changeset DELL:1763099652987-6
CREATE INDEX idx_user_sessions_active ON user_sessions(user_id, revoked_at);

-- changeset DELL:1763099652987-7
CREATE INDEX idx_user_sessions_refresh_token_hash ON user_sessions(refresh_token_hash);

-- changeset DELL:1763099652987-8
CREATE INDEX idx_user_sessions_session_token_hash ON user_sessions(session_token_hash);

-- changeset DELL:1763099652987-9
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id, created_at);

-- changeset DELL:1763099652987-10
CREATE INDEX idx_users_email ON users(email);

