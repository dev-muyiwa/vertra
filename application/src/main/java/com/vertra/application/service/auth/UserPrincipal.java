package com.vertra.application.service.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserPrincipal {
    private UUID id;
    private String email;
    private boolean active;

    public static UserPrincipal from(UUID id, String email) {
        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .active(true)
                .build();
    }
}
