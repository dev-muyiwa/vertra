package com.vertra.adapters.web.dto.response.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        String error,
        String message,
        String path,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ValidationError> validationErrors
) {
    public record ValidationError(
            String field,
            String message,
            Object rejectedValue
    ) {}

    public static ErrorResponse of(String error, String message, String path) {
        return new ErrorResponse(false, error, message, path, Instant.now(), null);
    }

    public static ErrorResponse withValidation(
            String error,
            String message,
            String path,
            List<ValidationError> validationErrors
    ) {
        return new ErrorResponse(false, error, message, path, Instant.now(), validationErrors);
    }
}
