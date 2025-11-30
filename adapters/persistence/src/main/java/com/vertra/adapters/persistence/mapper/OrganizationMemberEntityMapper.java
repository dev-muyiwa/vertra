package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.OrganizationMemberEntity;
import com.vertra.domain.model.organization.OrganizationMember;
import com.vertra.domain.model.organization.OrganizationRole;
import com.vertra.domain.vo.Uuid;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrganizationMemberEntityMapper {

    public OrganizationMemberEntity toEntity(OrganizationMember member) {
        return OrganizationMemberEntity.builder()
                .id(member.getId() != null ? member.getId().value() : null)
                .role(toRoleEntity(member.getRole()))
                .userId(member.getUserId() != null ? member.getUserId().value() : null)
                .orgId(member.getOrgId() != null ? member.getOrgId().value() : null)
                .invitedBy(member.getInvitedBy() != null ? member.getInvitedBy().value() : null)
                .joinedAt(member.getJoinedAt())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .deletedAt(member.getDeletedAt())
                .build();
    }

    public OrganizationMember toDomain(OrganizationMemberEntity entity) {
        return OrganizationMember.builder()
                .id(entity.getId() != null ? new Uuid(entity.getId()) : null)
                .role(toRoleDomain(entity.getRole()))
                .userId(entity.getUserId() != null ? new Uuid(entity.getUserId()) : null)
                .orgId(entity.getOrgId() != null ? new Uuid(entity.getOrgId()) : null)
                .invitedBy(entity.getInvitedBy() != null ? new Uuid(entity.getInvitedBy()) : null)
                .joinedAt(entity.getJoinedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private OrganizationMemberEntity.OrganizationRoleEntity toRoleEntity(OrganizationRole role) {
        if (role == null) {
            return null;
        }
        return switch (role) {
            case OWNER -> OrganizationMemberEntity.OrganizationRoleEntity.OWNER;
            case ADMIN -> OrganizationMemberEntity.OrganizationRoleEntity.ADMIN;
            case DEVELOPER -> OrganizationMemberEntity.OrganizationRoleEntity.DEVELOPER;
            case VIEWER -> OrganizationMemberEntity.OrganizationRoleEntity.VIEWER;
        };
    }

    private OrganizationRole toRoleDomain(OrganizationMemberEntity.OrganizationRoleEntity role) {
        if (role == null) {
            return null;
        }
        return switch (role) {
            case OWNER -> OrganizationRole.OWNER;
            case ADMIN -> OrganizationRole.ADMIN;
            case DEVELOPER -> OrganizationRole.DEVELOPER;
            case VIEWER -> OrganizationRole.VIEWER;
        };
    }
}
