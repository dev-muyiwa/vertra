package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
//    @Query("SELECT a FROM AuditLogEntity a WHERE a.organizationId = :orgId AND a.timestamp BETWEEN :from AND :to ORDER BY a.timestamp DESC")
//    List<AuditLogEntity> findByOrganizationAndTimestamp(
//            @Param("orgId") UUID organizationId,
//            @Param("from") Instant from,
//            @Param("to") Instant to
//    );
//
//    @Query("SELECT a FROM AuditLogEntity a WHERE a.action = :action AND a.timestamp BETWEEN :from AND :to ORDER BY a.timestamp DESC")
//    List<AuditLogEntity> findByActionAndTimestamp(
//            @Param("action") AuditAction action,
//            @Param("from") Instant from,
//            @Param("to") Instant to
//    );
//
//    @Query("SELECT a FROM AuditLogEntity a WHERE a.resourceType = :type AND a.resourceId = :id ORDER BY a.timestamp DESC")
//    List<AuditLogEntity> findByResource(
//            @Param("type") String resourceType,
//            @Param("id") UUID resourceId
//    );
//
//    @Query("SELECT a FROM AuditLogEntity a WHERE a.actorMemberId = :memberId AND a.timestamp BETWEEN :from AND :to ORDER BY a.timestamp DESC")
//    List<AuditLogEntity> findByActorMemberAndTimestamp(
//            @Param("memberId") UUID memberId,
//            @Param("from") Instant from,
//            @Param("to") Instant to
//    );
}
