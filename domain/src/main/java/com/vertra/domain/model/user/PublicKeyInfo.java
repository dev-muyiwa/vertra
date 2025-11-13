package com.vertra.domain.model.user;

import com.vertra.domain.exception.InvalidKeyException;

import java.security.MessageDigest;
import java.util.Base64;

public record PublicKeyInfo(
        String publicKey,
        String algorithm,
        String fingerprint
) {
    public static String extractFingerprint(String publicKey) {
        try {
            String[] parts = publicKey.split(" ");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid public key format");
            }
            byte[] keyBytes = Base64.getDecoder().decode(parts[1]);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(keyBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new InvalidKeyException("Invalid public key format");
        }
    }
}
