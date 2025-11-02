package com.vertra.domain.vo;

public record HashedPassword(String value) {
    public HashedPassword {
        if (value == null || !value.startsWith("$argon2id$")) {
            throw new IllegalArgumentException("Must be a valid Argon2id hashed password");
        }
    }
}
