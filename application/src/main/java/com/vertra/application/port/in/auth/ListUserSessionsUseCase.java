package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.Uuid;

import java.time.Instant;
import java.util.List;

public interface ListUserSessionsUseCase {

    List<SessionInfo> execute(ListUserSessionsQuery query);

    record ListUserSessionsQuery(
            Uuid userId
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }

    record SessionInfo(
            Uuid sessionId,
            Inet6 ipAddress,
            String userAgent,
            String deviceFingerprint,
            Instant createdAt,
            Instant lastActivityAt,
            Instant expiresAt,
            boolean isActive,
            String cliVersion
    ) {}
}
