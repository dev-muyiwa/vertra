package com.vertra.domain.vo;

import static java.util.Objects.requireNonNull;

public record PublicKeyFingerPrint(
        String value
) {
    public PublicKeyFingerPrint {
        requireNonNull(value, "Public Key FingerPrint value cannot be null");
    }
}
