package com.vertra.application.port.out.audit;

import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.Uuid;

import java.util.Map;

public interface AuditPort {
    void log(
            AuditAction action,
            Uuid organizationId,
            Uuid actorMemberId,
            Uuid actorServiceTokenId,
            ActorType actorType,
            Uuid projectId,
            Uuid configId,
            Uuid secretId,
            Inet6 ipAddress,
            String userAgent,
            Uuid requestId,
            Map<String, Object> metadata,
            boolean success,
            String message
    );
}
