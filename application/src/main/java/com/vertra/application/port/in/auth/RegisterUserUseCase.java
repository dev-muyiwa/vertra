package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.HashedPassword;
import com.vertra.domain.vo.Uuid;

import java.util.UUID;

public interface RegisterUserUseCase {

    RegisterUserResponse execute(RegisterUserCommand command);

    record RegisterUserCommand(
            String firstName,
            String lastName,
            Email email,
            String password
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

    record RegisterUserResponse(
            UUID id,
            String firstName,
            String lastName,
            String email
    ) {}
}
