package com.vertra.application.port.out.audit;

import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.vo.Inet6;

import java.util.Map;
import java.util.UUID;

public interface AuditPort {
    void log(
            AuditAction action,
            UUID organizationId,
            UUID actorMemberId,
            UUID actorServiceTokenId,
            ActorType actorType,
            UUID projectId,
            UUID configId,
            UUID secretId,
            Inet6 ipAddress,
            String userAgent,
            UUID requestId,
            Map<String, Object> metadata,
            boolean success,
            String message
    );
}
