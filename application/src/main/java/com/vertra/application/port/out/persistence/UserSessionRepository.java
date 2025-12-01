package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Uuid;

import java.util.List;
import java.util.Optional;


public interface UserSessionRepository {
    UserSession save(UserSession session);

    Optional<UserSession> findById(Uuid id);

    Optional<UserSession> findByTokenHash(HashedToken tokenHash);

    Optional<UserSession> findByRefreshTokenHash(HashedToken refreshTokenHash);

    Optional<UserSession> findByUserIdAndTokenHash(Uuid userId, HashedToken tokenHash);

    List<UserSession> findByUserId(Uuid userId);

    List<UserSession> findActiveByUserIdAndDeviceId(Uuid userId, Uuid deviceId);

    void revokeAllForUser(Uuid userId);

    void deleteExpiredSessions();
}
