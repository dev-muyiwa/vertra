package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Inet6;

public interface RefreshTokenUseCase {

    RefreshTokenResponse execute(RefreshTokenCommand command);

    record RefreshTokenCommand(
            String refreshToken,
            Inet6 ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Refresh token is required");
            }
        }
    }

    record RefreshTokenResponse(
            String accessToken,
            String refreshToken,
            int expiresIn
    ) {}
}

