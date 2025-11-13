package com.vertra.adapters.web.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        ErrorDetails error,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static <T> ApiResponse<T> error(String message, ErrorDetails error) {
        return new ApiResponse<>(false, null, message, error, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorDetails(
            String code,
            String field,
            Object rejectedValue
    ) {
    }
}
