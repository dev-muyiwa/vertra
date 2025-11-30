package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.OrganizationEntity;
import com.vertra.domain.model.organization.Organization;
import com.vertra.domain.vo.Uuid;
import org.springframework.stereotype.Component;

@Component
public class OrganizationEntityMapper {

    public OrganizationEntity toEntity(Organization organization) {
        return OrganizationEntity.builder()
                .id(organization.getId() != null ? organization.getId().value() : null)
                .name(organization.getName())
                .slug(organization.getSlug())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .deletedAt(organization.getDeletedAt())
                .build();
    }

    public Organization toDomain(OrganizationEntity entity) {
        return Organization.builder()
                .id(entity.getId() != null ? new Uuid(entity.getId()) : null)
                .name(entity.getName())
                .slug(entity.getSlug())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
