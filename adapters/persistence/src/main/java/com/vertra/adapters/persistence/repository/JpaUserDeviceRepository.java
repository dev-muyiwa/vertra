package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserDeviceRepository extends JpaRepository<UserDeviceEntity, UUID> {

    @Query("SELECT d FROM UserDeviceEntity d WHERE d.userId = :userId AND d.deviceId = :deviceId AND d.revokedAt IS NULL")
    Optional<UserDeviceEntity> findActiveByUserIdAndDeviceId(
            @Param("userId") UUID userId,
            @Param("deviceId") String deviceId
    );

    @Query("SELECT d FROM UserDeviceEntity d WHERE d.userId = :userId")
    List<UserDeviceEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT d FROM UserDeviceEntity d WHERE d.userId = :userId AND d.revokedAt IS NULL")
    List<UserDeviceEntity> findActiveByUserId(@Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM UserDeviceEntity d WHERE d.deviceFingerprint = :fingerprint AND d.revokedAt IS NULL")
    boolean existsByDeviceFingerprint(@Param("fingerprint") String fingerprint);

    @Modifying
    @Query("UPDATE UserDeviceEntity d SET d.revokedAt = CURRENT_TIMESTAMP WHERE d.id = :id AND d.revokedAt IS NULL")
    int revokeById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE UserDeviceEntity d SET d.revokedAt = CURRENT_TIMESTAMP WHERE d.userId = :userId AND d.revokedAt IS NULL")
    int revokeAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM UserDeviceEntity d WHERE d.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
