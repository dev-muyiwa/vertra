package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.entity.UserDeviceEntity;
import com.vertra.adapters.persistence.mapper.UserDeviceEntityMapper;
import com.vertra.adapters.persistence.repository.JpaUserDeviceRepository;
import com.vertra.application.port.out.persistence.UserDeviceRepository;
import com.vertra.domain.model.user.UserDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeviceRepositoryAdapter implements UserDeviceRepository {

    private final JpaUserDeviceRepository jpaRepo;
    private final UserDeviceEntityMapper mapper;

    @Override
    public UserDevice save(UserDevice device) {
        log.debug("Saving user device: userId={}, deviceId={}", device.getUserId(), device.getDeviceId());

        UserDeviceEntity entity = mapper.toEntity(device);
        UserDeviceEntity savedEntity = jpaRepo.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserDevice> findById(UUID id) {
        log.debug("Finding device by id: {}", id);

        return jpaRepo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserDevice> findByUserIdAndDeviceId(UUID userId, String deviceId) {
        log.debug("Finding device by userId={} and deviceId={}", userId, deviceId);

        return jpaRepo.findActiveByUserIdAndDeviceId(userId, deviceId)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserDevice> findByUserId(UUID userId) {
        log.debug("Finding all devices by userId: {}", userId);

        return jpaRepo.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserDevice> findActiveByUserId(UUID userId) {
        log.debug("Finding active devices by userId: {}", userId);

        return jpaRepo.findActiveByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDeviceFingerprint(String fingerprint) {
        log.debug("Checking existence by fingerprint: {}", fingerprint);

        return jpaRepo.existsByDeviceFingerprint(fingerprint);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting device by id: {}", id);

        jpaRepo.deleteById(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        log.debug("Deleting all devices by userId: {}", userId);

        jpaRepo.deleteByUserId(userId);
    }
}
