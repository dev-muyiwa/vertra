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
                .id(session.id())
                .userId(session.userId())
                .sessionTokenHash(session.sessionTokenHash().toString())
                .refreshTokenHash(session.refreshTokenHash().toString())
                .userAgent(session.userAgent())
                .ipAddress(session.ipAddress().toString())
                .expiresAt(session.expiresAt())
                .createdAt(session.createdAt())
                .revokedAt(session.revokedAt())
                .build();
    }

    public UserSession toDomain(UserSessionEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserSession.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sessionTokenHash(new HashedToken(entity.getSessionTokenHash()))
                .refreshTokenHash(new HashedToken(entity.getRefreshTokenHash()))
                .userAgent(entity.getUserAgent())
                .ipAddress(Inet6.parse(entity.getIpAddress()))
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}
