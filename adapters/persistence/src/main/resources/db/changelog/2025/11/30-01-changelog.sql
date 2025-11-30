-- liquibase formatted sql

-- changeset DELL:1764500422645-9
CREATE TABLE organization_members (id UUID NOT NULL, role VARCHAR(20) NOT NULL, user_id UUID NOT NULL, org_id UUID NOT NULL, invited_by UUID, joined_at TIMESTAMP WITHOUT TIME ZONE, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, deleted_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_organization_members PRIMARY KEY (id));

-- changeset DELL:1764500422645-10
CREATE TABLE organizations (id UUID NOT NULL, name VARCHAR(255) NOT NULL, slug VARCHAR(100) NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, deleted_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_organizations PRIMARY KEY (id));

-- changeset DELL:1764500422645-11
ALTER TABLE organizations ADD CONSTRAINT uc_organizations_slug UNIQUE (slug);

-- changeset DELL:1764500422645-12
CREATE INDEX idx_org_members_org_id ON organization_members(org_id);

-- changeset DELL:1764500422645-13
CREATE INDEX idx_org_members_user_id ON organization_members(user_id);

-- changeset DELL:1764500422645-14
CREATE INDEX idx_org_members_user_org ON organization_members(user_id, org_id);

-- changeset DELL:1764500422645-15
CREATE UNIQUE INDEX idx_organizations_slug ON organizations(slug);
