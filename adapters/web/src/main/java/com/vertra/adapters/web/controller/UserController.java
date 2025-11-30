package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.response.common.ApiResponse;
import com.vertra.adapters.web.dto.response.user.CurrentUserResponse;
import com.vertra.application.port.in.user.GetCurrentUserUseCase;
import com.vertra.application.port.out.security.SecurityContextPort;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SecurityContextPort securityContext;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser() {
        UUID userId = securityContext.getCurrentUserId();

        var query = new GetCurrentUserUseCase.GetCurrentUserQuery(
                new Uuid(userId)
        );

        var userInfo = getCurrentUserUseCase.execute(query);

        var response = new CurrentUserResponse(
                userInfo.id().value(),
                userInfo.firstName(),
                userInfo.lastName(),
                userInfo.email(),
                userInfo.profilePictureUrl(),
                userInfo.hasOrganization(),
                userInfo.isEmailVerified()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
