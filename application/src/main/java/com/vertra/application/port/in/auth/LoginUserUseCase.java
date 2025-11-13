package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Inet6;

import java.util.UUID;

public interface LoginUserUseCase {

    LoginUserResponse execute(LoginUserCommand command);

    record LoginUserCommand(
            Email email,
            String password,
            Inet6 ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (email == null || email.value().isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password hash is required");
            }
        }
    }

    record LoginUserResponse(
            String accessToken,
            String refreshToken,
            int expiresIn,
            UserInfo user
    ) {
        public record UserInfo(
                UUID id,
                String firstName,
                String lastName,
                String email
        ) {
        }
    }
}
