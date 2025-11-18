package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Inet6;

import java.util.UUID;

public interface VerifyEmailUseCase {

    void execute(VerifyEmailCommand command);

    record VerifyEmailCommand(
            UUID userId,
            String token,
            Inet6 ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Verification token is required");
            }
        }
    }


}
