package com.vertra.application.service;

import com.vertra.application.command.LoginUserCommand;
import com.vertra.application.command.RegisterUserCommand;
import com.vertra.application.usecase.AuthUseCase;
import com.vertra.domain.model.user.User;
import com.vertra.domain.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AuthenticationService authService;

    @Override
    @Transactional
    public User register(RegisterUserCommand cmd) {
        return authService.register(cmd.email(), cmd.password());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationService.AuthTokens login(LoginUserCommand cmd) {
        return authService.login(cmd.email(), cmd.password());
    }
}
