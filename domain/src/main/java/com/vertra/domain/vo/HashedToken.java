package com.vertra.domain.vo;

public record HashedToken(String value) {
    public HashedToken {
        if (value == null || value.length() < 64) {
            throw new IllegalArgumentException("Invalid token hash");
        }
    }
}
