package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.request.device.RegisterDeviceRequest;
import com.vertra.adapters.web.dto.response.device.RegisterDeviceResponse;
import com.vertra.application.port.in.device.RegisterDeviceUseCase;
import com.vertra.application.port.out.security.SecurityContextPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final RegisterDeviceUseCase registerDeviceUseCase;
    private final SecurityContextPort securityContext;

    @PostMapping
    public ResponseEntity<RegisterDeviceResponse> registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID userId = securityContext.getCurrentUserId();

        log.info("POST /devices - deviceId={}, userId={}", request.deviceId(), userId);

        var command = new RegisterDeviceUseCase.RegisterDeviceCommand(
                userId,
                request.deviceId(),
                request.encryptedPrivateKey(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        var result = registerDeviceUseCase.execute(command);

        return ResponseEntity.ok(
                new RegisterDeviceResponse(
                        result.success(),
                        result.message(),
                        result.deviceId()
                )
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
