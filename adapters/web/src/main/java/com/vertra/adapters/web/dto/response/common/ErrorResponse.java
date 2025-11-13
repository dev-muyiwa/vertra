package com.vertra.adapters.web.dto.response.common;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<ValidationError> validationErrors
) {
    public record ValidationError(
            String field,
            String message,
            Object rejectedValue
    ) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }

    public static ErrorResponse withValidation(
            int status,
            String error,
            String message,
            String path,
            List<ValidationError> validationErrors
    ) {
        return new ErrorResponse(status, error, message, path, Instant.now(), validationErrors);
    }
}
