package com.vertra.domain.vo;

public record HashedToken(String value) {
    public HashedToken {
        if (value == null) {
            throw new IllegalArgumentException("Invalid token hash");
        }
    }
}
