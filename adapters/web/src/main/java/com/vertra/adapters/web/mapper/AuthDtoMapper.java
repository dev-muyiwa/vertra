package com.vertra.adapters.web.mapper;

import com.vertra.adapters.web.dto.request.auth.LoginUserRequest;
import com.vertra.adapters.web.dto.request.auth.RegisterUserRequest;
import com.vertra.adapters.web.dto.request.auth.StartEmailVerificationRequest;
import com.vertra.adapters.web.dto.response.auth.LoginUserResponse;
import com.vertra.adapters.web.dto.response.auth.RegisterUserResponse;
import com.vertra.application.port.in.auth.LoginUserUseCase;
import com.vertra.application.port.in.auth.RegisterUserUseCase;
import com.vertra.application.port.in.auth.StartEmailVerificationUseCase;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Inet6;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoMapper {

    public RegisterUserUseCase.RegisterUserCommand toRegisterCommand(RegisterUserRequest request) {
        return new RegisterUserUseCase.RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                new Email(request.email()),
                request.password()
        );
    }

    public RegisterUserResponse toRegisterResponse(RegisterUserUseCase.RegisterUserResponse result) {
        return new RegisterUserResponse(
                result.id(),
                result.firstName(),
                result.lastName(),
                result.email()
        );
    }

    public LoginUserUseCase.LoginUserCommand toLoginCommand(
            LoginUserRequest request,
            String ipAddress,
            String userAgent
    ) {
        return new LoginUserUseCase.LoginUserCommand(
                new Email(request.email()),
                request.password(),
                Inet6.parse(ipAddress),
                userAgent
        );
    }

    public LoginUserResponse toLoginResponse(LoginUserUseCase.LoginUserResponse result) {
        return new LoginUserResponse(
                result.user().id(),
                result.user().firstName(),
                result.user().lastName(),
                result.user().email(),
                new LoginUserResponse.SessionToken(
                        result.accessToken(),
                        result.refreshToken(),
                        result.expiresIn()
                )
        );
    }

    public StartEmailVerificationUseCase.StartEmailVerificationCommand toStartEmailVerificationCommand(StartEmailVerificationRequest request, String ipAddress, String userAgent) {
        return new StartEmailVerificationUseCase.StartEmailVerificationCommand(
                request.redirectPath(),
                Inet6.parse(ipAddress),
                userAgent
        );
    }

}
