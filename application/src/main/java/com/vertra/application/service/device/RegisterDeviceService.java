package com.vertra.application.service.device;

import com.vertra.application.port.in.device.RegisterDeviceUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.persistence.UserDeviceRepository;
import com.vertra.domain.exception.ResourceNotFoundException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.UserDevice;
import com.vertra.domain.vo.Inet6;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterDeviceService implements RegisterDeviceUseCase {

    private final UserDeviceRepository deviceRepository;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public RegisterDeviceResponse execute(RegisterDeviceCommand command) {
        log.info("Registering device with encrypted private key: userId={}, deviceId={}", command.userId(), command.deviceId());

        command.validate();

        // Step 1: Find device
        UserDevice device = deviceRepository.findByUserIdAndDeviceId(command.userId(), command.deviceId())
                .orElseThrow(() -> {
                    log.error("Device not found: userId={}, deviceId={}", command.userId(), command.deviceId());
                    return new ResourceNotFoundException("Device not found: " + command.deviceId());
                });

        // Step 2: Verify encrypted_private_key is NULL
        if (device.getEncryptedPrivateKey() != null) {
            log.error("Device already has encrypted private key: deviceId={}", command.deviceId());
            throw new IllegalStateException("Device already registered with encrypted private key");
        }

        // Step 3: Update device with encrypted key
        UserDevice updatedDevice = device.withEncryptedPrivateKey(command.encryptedPrivateKey())
                .withTrusted(true);
        UserDevice savedDevice = deviceRepository.save(updatedDevice);

        log.info("Device registered successfully: deviceId={}", savedDevice.getDeviceId());

        // Step 4: Audit log
        auditPort.log(
                AuditAction.DEVICE_REGISTERED,
                null,
                command.userId(),
                null,
                ActorType.USER,
                null,
                null,
                null,
                Inet6.parse(command.ipAddress()),
                command.userAgent(),
                UUID.randomUUID(),
                Map.of(
                        "device_id", savedDevice.getDeviceId(),
                        "device_name", savedDevice.getDeviceName(),
                        "recovery", true
                ),
                true,
                "Device registered with encrypted private key: " + savedDevice.getDeviceId()
        );

        return new RegisterDeviceResponse(
                true,
                "Device registered successfully",
                savedDevice.getDeviceId()
        );
    }
}
