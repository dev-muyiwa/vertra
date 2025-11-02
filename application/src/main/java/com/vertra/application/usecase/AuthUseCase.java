package com.vertra.application.usecase;

import com.vertra.application.command.LoginUserCommand;
import com.vertra.application.command.RegisterUserCommand;
import com.vertra.domain.model.user.User;
import com.vertra.domain.service.AuthenticationService;

public interface AuthUseCase {
    User register(RegisterUserCommand cmd);

    AuthenticationService.AuthTokens login(LoginUserCommand cmd);
}
