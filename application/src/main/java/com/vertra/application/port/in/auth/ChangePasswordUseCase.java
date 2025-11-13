package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.HashedPassword;
import com.vertra.domain.vo.Uuid;

public interface ChangePasswordUseCase {

    void execute(ChangePasswordCommand command);

    record ChangePasswordCommand(
            Uuid userId,
            HashedPassword currentPasswordHash,
            HashedPassword newPasswordHash,
            String newEncryptedPrivateKey,
            String newPrivateKeyIv,
            String newKeyDerivationSalt
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (currentPasswordHash == null || currentPasswordHash.value().isBlank()) {
                throw new IllegalArgumentException("Current password is required");
            }
            if (newPasswordHash == null || newPasswordHash.value().isBlank()) {
                throw new IllegalArgumentException("New password is required");
            }
            if (newEncryptedPrivateKey == null || newEncryptedPrivateKey.isBlank()) {
                throw new IllegalArgumentException("New encrypted private key is required");
            }
        }
    }
}
