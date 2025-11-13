package com.vertra.domain.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EncryptionMetadata {
    String algorithm;
    String keyEncryptionAlgorithm;
    int version;
    PublicKeyFingerPrint publicKeyFingerprint;

    public static EncryptionMetadata defaultMetadata(String publicKeyFingerprint) {
        return EncryptionMetadata.builder()
                .algorithm("AES-256-GCM")
                .keyEncryptionAlgorithm("RSA-OAEP-SHA256")
                .version(1)
                .publicKeyFingerprint(new PublicKeyFingerPrint(publicKeyFingerprint))
                .build();
    }

    public void validate() {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("Encryption algorithm is required");
        }
        if (version < 1) {
            throw new IllegalArgumentException("Encryption version must be >= 1");
        }
        if (publicKeyFingerprint == null || publicKeyFingerprint.value().isBlank()) {
            throw new IllegalArgumentException("Public key fingerprint is required");
        }
    }

    public boolean isSupported() {
        return "AES-256-GCM".equals(algorithm) && "RSA-OAEP-SHA256".equals(keyEncryptionAlgorithm);
    }
}
