package com.vertra.adapters.web.dto.response.device;

public record RegisterDeviceResponse(
        boolean success,
        String message,
        String deviceId
) {}
