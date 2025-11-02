package com.vertra.domain.vo;



import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record Uuid(UUID value) {

    public Uuid {
        if (value == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
    }

    public static Uuid random() {
        return new Uuid(UUID.randomUUID());
    }

    public static Uuid fromString(String s) {
        return new Uuid(UUID.fromString(s));
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }
}
