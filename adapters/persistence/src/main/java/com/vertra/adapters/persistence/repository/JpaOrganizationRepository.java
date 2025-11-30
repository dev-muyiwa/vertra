package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {

    @Query("SELECT o FROM OrganizationEntity o WHERE o.id = :id AND o.deletedAt IS NULL")
    Optional<OrganizationEntity> findUndeletedById(@Param("id") UUID id);

    @Query("SELECT o FROM OrganizationEntity o WHERE o.slug = :slug AND o.deletedAt IS NULL")
    Optional<OrganizationEntity> findUndeletedBySlug(@Param("slug") String slug);

    @Query("SELECT o FROM OrganizationEntity o WHERE o.id IN :ids AND o.deletedAt IS NULL")
    List<OrganizationEntity> findAllByIdIn(@Param("ids") List<UUID> ids);

    boolean existsBySlug(String slug);
}
