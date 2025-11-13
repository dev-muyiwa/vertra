package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final RegisterUserUseCase register;
    private final LoginUserService login;

    public RegisterUserUseCase.RegisterUserResponse registerUser(RegisterUserUseCase.RegisterUserCommand cmd) {
        return register.execute(cmd);
    }

    public LoginUserService.LoginUserResponse loginUser(LoginUserService.LoginUserCommand cmd) {
        return login.execute(cmd);
    }
}
