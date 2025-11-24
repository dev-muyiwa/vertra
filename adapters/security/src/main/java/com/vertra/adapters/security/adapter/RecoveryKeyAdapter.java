package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.RecoveryKeyPort;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class RecoveryKeyAdapter implements RecoveryKeyPort {

    private static final int SALT_LENGTH = 32;
    private static final int ARGON2_ITERATIONS = 3;
    private static final int ARGON2_MEMORY = 65536; // 64 MB
    private static final int ARGON2_PARALLELISM = 4;
    private static final int HASH_LENGTH = 32;

    private final Argon2 argon2;
    private final SecureRandom secureRandom;

    public RecoveryKeyAdapter() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateRecoverySalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
    }

    @Override
    public String hashRecoveryKey(String recoveryKey, String salt) {
        if (recoveryKey == null || recoveryKey.isBlank()) {
            throw new IllegalArgumentException("Recovery key cannot be null or empty");
        }
        if (salt == null || salt.isBlank()) {
            throw new IllegalArgumentException("Salt cannot be null or empty");
        }

        try {
            // Combine recovery key with salt for additional entropy
            String combined = recoveryKey + salt;
            return argon2.hash(
                    ARGON2_ITERATIONS,
                    ARGON2_MEMORY,
                    ARGON2_PARALLELISM,
                    combined.toCharArray()
            );
        } catch (Exception e) {
            log.error("Failed to hash recovery key", e);
            throw new RuntimeException("Failed to hash recovery key", e);
        }
    }

    @Override
    public boolean verifyRecoveryKey(String recoveryKey, String salt, String storedHash) {
        if (recoveryKey == null || salt == null || storedHash == null) {
            return false;
        }

        try {
            String combined = recoveryKey + salt;
            return argon2.verify(storedHash, combined.toCharArray());
        } catch (Exception e) {
            log.error("Failed to verify recovery key", e);
            return false;
        }
    }
}
