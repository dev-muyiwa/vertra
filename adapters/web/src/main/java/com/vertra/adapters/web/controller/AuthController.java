package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.request.auth.LoginUserRequest;
import com.vertra.adapters.web.dto.request.auth.RegisterUserRequest;
import com.vertra.adapters.web.dto.response.auth.LoginUserResponse;
import com.vertra.adapters.web.dto.response.auth.RegisterUserResponse;
import com.vertra.adapters.web.dto.response.common.ApiResponse;
import com.vertra.adapters.web.mapper.AuthDtoMapper;
import com.vertra.application.port.in.auth.LoginUserUseCase;
import com.vertra.application.port.in.auth.RegisterUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final AuthDtoMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(@Valid @RequestBody RegisterUserRequest data) {
        var cmd = mapper.toRegisterCommand(data);
        var result = registerUserUseCase.execute(cmd);
        var res = mapper.toRegisterResponse(result);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(res, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginUserResponse>> login(
            @Valid @RequestBody LoginUserRequest data,
            HttpServletRequest req
    ) {
        var cmd = mapper.toLoginCommand(
                data,
                getClientIp(req),
                req.getHeader("User-Agent")
        );
        var result = loginUserUseCase.execute(cmd);
        var res = mapper.toLoginResponse(result);

        return ResponseEntity
                .ok(ApiResponse.success(res, "User logged in successfully"));
    }

    //    TODO("Move to a utility class that would be reusable across controllers")
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
