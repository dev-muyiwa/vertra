package com.vertra.domain.model.organization;

import com.vertra.domain.exception.ForbiddenException;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;

@Getter
@Builder
@With
public class OrganizationMember {
    Uuid id;
    OrganizationRole role;

    Uuid userId;
    Uuid orgId;
    Uuid invitedBy;

    Instant joinedAt;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;

    public boolean isActive() {
        return deletedAt == null && joinedAt != null;
    }

    public void leave() {
        this.deletedAt = Instant.now();
    }

    public void changeRole(OrganizationRole newRole, OrganizationRole changerRole) {
        if (changerRole != OrganizationRole.OWNER) {
            throw new ForbiddenException("Only owners can change member roles");
        }

        this.role = newRole;
        this.updatedAt = Instant.now();
    }

    public boolean canCreateProjects() {
        return role == OrganizationRole.OWNER || role == OrganizationRole.ADMIN;
    }

    public boolean canInviteMembers() {
        return role == OrganizationRole.OWNER || role == OrganizationRole.ADMIN;
    }

    public boolean canRemoveMembers() {
        return role == OrganizationRole.OWNER || role == OrganizationRole.ADMIN;
    }

    public boolean canManageOrganization() {
        return role == OrganizationRole.OWNER;
    }
}
