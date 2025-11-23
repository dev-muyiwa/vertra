package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Inet6;

public interface VerifyEmailUseCase {

    void execute(VerifyEmailCommand command);

    record VerifyEmailCommand(
            String token,
            Inet6 ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Verification token is required");
            }
        }
    }


}
