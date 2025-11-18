package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface JpaOtpRepository extends JpaRepository<OtpEntity, UUID> {
    @Query("SELECT t FROM OtpEntity t WHERE t.token = :token AND t.userId = :userId")
    Optional<OtpEntity> findByToken(String token, UUID userId);

    @Query("SELECT t FROM OtpEntity t WHERE t.userId = :userId ORDER BY t.createdAt DESC LIMIT 1")
    Optional<OtpEntity> findLatestByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM OtpEntity t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM OtpEntity t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
