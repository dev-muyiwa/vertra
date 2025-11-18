-- liquibase formatted sql

-- changeset DELL:1763102739433-7
CREATE TABLE audit_logs (id UUID NOT NULL, action VARCHAR(100) NOT NULL, organization_id UUID, actor_member_id UUID, actor_service_token_id UUID, resource_type VARCHAR(100), resource_id UUID, ip_address VARCHAR(45), user_agent TEXT, metadata JSONB, success BOOLEAN NOT NULL, message TEXT, timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT pk_audit_logs PRIMARY KEY (id));

-- changeset DELL:1763102739433-8
CREATE INDEX idx_audit_logs_action ON audit_logs(action, timestamp);

-- changeset DELL:1763102739433-9
CREATE INDEX idx_audit_logs_actor_member ON audit_logs(actor_member_id, timestamp);

-- changeset DELL:1763102739433-10
CREATE INDEX idx_audit_logs_organization ON audit_logs(organization_id, timestamp);

-- changeset DELL:1763102739433-11
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);

-- changeset DELL:1763102739433-12
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp DESC);

