package com.vertra.domain.model.config;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ConfigMember(
        Uuid id,
        ConfigPermission permission,

        Uuid configId,
        Uuid memberId,
        Uuid grantedBy,

        Instant grantedAt,
        Instant updatedAt,
        Instant revokedAt
) {
}
