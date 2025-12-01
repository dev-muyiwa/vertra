package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.UserSessionEntity;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {
    @Query("SELECT s FROM UserSessionEntity s WHERE s.sessionTokenHash = :tokenHash")
    Optional<UserSessionEntity> findBySessionTokenHash(@Param("tokenHash") String tokenHash);

    @Query("SELECT s FROM UserSessionEntity s WHERE s.refreshTokenHash = :refreshTokenHash")
    Optional<UserSessionEntity> findByRefreshTokenHash(@Param("refreshTokenHash") String refreshTokenHash);

    @Query("SELECT s FROM UserSessionEntity s WHERE s.userId = :userId AND s.sessionTokenHash = :tokenHash")
    Optional<UserSessionEntity> findByUserIdAndSessionTokenHash(
            @Param("userId") UUID userId,
            @Param("tokenHash") String tokenHash
    );

    @Query("SELECT s FROM UserSessionEntity s WHERE s.userId = :userId ORDER BY s.createdAt DESC")
    List<UserSessionEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT s FROM UserSessionEntity s WHERE s.userId = :userId AND s.deviceId = :deviceId AND s.revokedAt IS NULL AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<UserSessionEntity> findActiveByUserIdAndDeviceId(
            @Param("userId") UUID userId,
            @Param("deviceId") UUID deviceId,
            @Param("now") Instant now
    );

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.revokedAt = :revokedAt WHERE s.userId = :userId AND s.revokedAt IS NULL")
    void revokeAllForUser(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

    @Modifying
    @Query("DELETE FROM UserSessionEntity s WHERE s.expiresAt < :now AND s.revokedAt IS NOT NULL")
    void deleteExpiredSessions(@Param("now") Instant now);
}
