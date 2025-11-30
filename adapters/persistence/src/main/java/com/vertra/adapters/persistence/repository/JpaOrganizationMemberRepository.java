package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.OrganizationMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOrganizationMemberRepository extends JpaRepository<OrganizationMemberEntity, UUID> {

    @Query("SELECT om FROM OrganizationMemberEntity om WHERE om.userId = :userId AND om.deletedAt IS NULL AND om.joinedAt IS NOT NULL")
    List<OrganizationMemberEntity> findActiveByUserId(@Param("userId") UUID userId);

    @Query("SELECT om FROM OrganizationMemberEntity om WHERE om.userId = :userId AND om.orgId = :orgId")
    Optional<OrganizationMemberEntity> findByUserIdAndOrgId(@Param("userId") UUID userId, @Param("orgId") UUID orgId);

    @Query("SELECT COUNT(om) > 0 FROM OrganizationMemberEntity om WHERE om.userId = :userId AND om.deletedAt IS NULL AND om.joinedAt IS NOT NULL")
    boolean existsActiveByUserId(@Param("userId") UUID userId);
}
