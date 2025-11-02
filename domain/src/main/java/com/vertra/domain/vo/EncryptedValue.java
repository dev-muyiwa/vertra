package com.vertra.domain.vo;

public record EncryptedValue(byte[] cipherText, byte[] iv, byte[] authTag) {
    public EncryptedValue {
        cipherText = cipherText.clone();
        iv = iv.clone();
        authTag = authTag.clone();
    }
}

