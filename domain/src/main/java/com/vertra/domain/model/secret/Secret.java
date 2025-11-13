package com.vertra.domain.model.secret;

import com.vertra.domain.exception.SecretKeyImmutableException;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.audit.AuditLog;
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
}
