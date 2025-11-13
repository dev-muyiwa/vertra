package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Uuid;

public interface RevokeSessionUseCase {

    void execute(RevokeSessionCommand command);

    record RevokeSessionCommand(
            Uuid userId,
            Uuid sessionId
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (sessionId == null) {
                throw new IllegalArgumentException("Session ID is required");
            }
        }
    }
}
