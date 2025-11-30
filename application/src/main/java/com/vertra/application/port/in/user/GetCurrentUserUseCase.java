package com.vertra.application.port.in.user;

import com.vertra.domain.vo.Uuid;

public interface GetCurrentUserUseCase {

    UserInfo execute(GetCurrentUserQuery query);

    record GetCurrentUserQuery(
            Uuid userId
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }

    record UserInfo(
            Uuid id,
            String firstName,
            String lastName,
            String email,
            String profilePictureUrl,
            boolean hasOrganization,
            boolean isEmailVerified
    ) {
    }
}
