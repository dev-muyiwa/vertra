package com.vertra.domain.model.project;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ProjectMember(
        Uuid id,
        ProjectPermission permission,

        Uuid projectId,
        Uuid memberId,

        Uuid grantedBy,
        Instant grantedAt,
        Instant updatedAt,
        Instant revokedAt
) {
}
