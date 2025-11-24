package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.adapters.persistence.entity.UserSessionEntity;
import com.vertra.domain.model.user.User;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Inet6;
import org.springframework.stereotype.Component;

@Component
public class UserSessionEntityMapper {
    public UserSessionEntity toEntity(UserSession session) {
        if (session == null) {
            return null;
        }

        return UserSessionEntity.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .deviceId(session.getDeviceId())
                .sessionTokenHash(session.getSessionTokenHash().toString())
                .refreshTokenHash(session.getRefreshTokenHash().toString())
                .userAgent(session.getUserAgent())
                .ipAddress(session.getIpAddress() != null ? session.getIpAddress().toString() : null)
                .deviceFingerprint(session.getDeviceFingerprint())
                .expiresAt(session.getExpiresAt())
                .lastActivityAt(session.getLastActivityAt())
                .createdAt(session.getCreatedAt())
                .revokedAt(session.getRevokedAt())
                .build();
    }

    public UserSession toDomain(UserSessionEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserSession.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .deviceId(entity.getDeviceId())
                .sessionTokenHash(new HashedToken(entity.getSessionTokenHash()))
                .refreshTokenHash(new HashedToken(entity.getRefreshTokenHash()))
                .userAgent(entity.getUserAgent())
                .ipAddress(Inet6.parse(entity.getIpAddress()))
                .deviceFingerprint(entity.getDeviceFingerprint())
                .expiresAt(entity.getExpiresAt())
                .lastActivityAt(entity.getLastActivityAt())
                .createdAt(entity.getCreatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}
