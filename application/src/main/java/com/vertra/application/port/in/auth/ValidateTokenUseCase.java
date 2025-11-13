package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.Uuid;

public interface ValidateTokenUseCase {

    ValidateTokenResponse execute(ValidateTokenCommand command);

    record ValidateTokenCommand(
            String token,
            Inet6 ipAddress
    ) {
        public void validate() {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is required");
            }
        }
    }

    record ValidateTokenResponse(
            boolean valid,
            Uuid userId,
            Email email,
            String reason
    ) {
    }
}
