package com.vertra.domain.model.secret;

import com.vertra.domain.exception.SecretKeyImmutableException;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.audit.AuditLog;
import com.vertra.domain.port.out.AuditLogRepository;
import com.vertra.domain.vo.SecretKey;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Secret(
        Uuid id,
        String key,
        String description,
        SecretType type,

        Uuid configId,
        Uuid currentVersionId,
        Uuid createdBy,

        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public Secret rotateToVersion(Uuid newVersionId, Uuid actorId, AuditLogRepository audit) {
        audit.append(AuditLog.builder()
                .action(AuditAction.SECRET_UPDATED)
                .actorMemberId(actorId)
                .secretId(id)
                .build());
        return this.toBuilder()
                .currentVersionId(newVersionId)
                .updatedAt(Instant.now())
                .build();
    }

    public Secret renameKey(SecretKey newKey, Uuid actorId, AuditLogRepository audit) {
        throw new SecretKeyImmutableException();
    }
}
