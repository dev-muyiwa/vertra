package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.UserDeviceEntity;
import com.vertra.domain.model.user.UserDevice;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceEntityMapper {

    public UserDeviceEntity toEntity(UserDevice device) {
        if (device == null) {
            return null;
        }

        return UserDeviceEntity.builder()
                .id(device.getId())
                .userId(device.getUserId())
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceFingerprint(device.getDeviceFingerprint())
                .encryptedPrivateKey(device.getEncryptedPrivateKey())
                .isTrusted(device.isTrusted())
                .createdAt(device.getCreatedAt())
                .lastUsedAt(device.getLastUsedAt())
                .revokedAt(device.getRevokedAt())
                .build();
    }

    public UserDevice toDomain(UserDeviceEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDevice.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .deviceId(entity.getDeviceId())
                .deviceName(entity.getDeviceName())
                .deviceFingerprint(entity.getDeviceFingerprint())
                .encryptedPrivateKey(entity.getEncryptedPrivateKey())
                .isTrusted(entity.isTrusted())
                .createdAt(entity.getCreatedAt())
                .lastUsedAt(entity.getLastUsedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}
