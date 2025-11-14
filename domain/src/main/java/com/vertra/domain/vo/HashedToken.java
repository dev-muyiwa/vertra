package com.vertra.domain.vo;

import org.jetbrains.annotations.NotNull;

public record HashedToken(String value) {
    public HashedToken {
        if (value == null) {
            throw new IllegalArgumentException("Invalid token hash");
        }
    }

    @Override
    public @NotNull String toString() {
        return value;
    }
}
