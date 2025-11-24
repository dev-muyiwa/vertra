package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.user.UserDevice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDeviceRepository {
    UserDevice save(UserDevice device);

    Optional<UserDevice> findById(UUID id);

    Optional<UserDevice> findByUserIdAndDeviceId(UUID userId, String deviceId);

    List<UserDevice> findByUserId(UUID userId);

    List<UserDevice> findActiveByUserId(UUID userId);

    boolean existsByDeviceFingerprint(String fingerprint);

    void deleteById(UUID id);

    void deleteByUserId(UUID userId);
}
