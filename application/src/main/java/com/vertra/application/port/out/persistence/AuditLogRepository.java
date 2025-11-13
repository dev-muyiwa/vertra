package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.audit.AuditLog;
import com.vertra.domain.vo.Uuid;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByOrganization(Uuid organizationId, Instant from, Instant to);

    List<AuditLog> findByAction(AuditAction action, Instant from, Instant to);

    List<AuditLog> findByResource(String resourceType, Uuid resourceId);

    List<AuditLog> findByActorMember(Uuid memberId, Instant from, Instant to);
}
