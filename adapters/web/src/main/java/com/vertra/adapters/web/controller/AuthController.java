package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.AuthResponse;
import com.vertra.application.command.LoginUserCommand;
import com.vertra.application.command.RegisterUserCommand;
import com.vertra.application.usecase.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserCommand cmd) {
        var user = authUseCase.register(cmd);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserCommand cmd) {
        var tokens = authUseCase.login(cmd);
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));
    }
}
