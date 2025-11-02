package com.vertra.domain.vo;

import com.vertra.domain.exception.InvalidKeyException;

public record SecretKey(String key) {

    public SecretKey {
        if (key == null || key.isEmpty() || !key.matches("^(?!_)(?!.*__)[A-Z0-9_]{3,255}(?<!_)$")) {
            throw new InvalidKeyException(
                    "Secret key must be 3-255 characters long, contain only uppercase letters, numbers, and underscores, " +
                            "cannot start or end with an underscore, and cannot have consecutive underscores"
            );
        }

        if (key.startsWith("VERTRA")) {
            throw new InvalidKeyException("Secret key cannot start with reserved prefix 'VERTRA'");
        }
    }
}
