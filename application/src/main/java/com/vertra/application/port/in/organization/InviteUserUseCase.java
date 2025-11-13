package com.vertra.application.port.in.organization;

import com.vertra.domain.model.organization.OrganizationRole;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Uuid;

public interface InviteUserUseCase {
    void execute(InviteUserCommand command);

    record InviteUserCommand(
            Uuid organizationId,
            Email email,
            OrganizationRole role,
            Uuid currentUserId,
            Uuid currentMemberId
    ) {
        public void validate() {
            if (organizationId == null) {
                throw new IllegalArgumentException("Organization ID is required");
            }
            if (email == null || email.value().isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (role == null) {
                throw new IllegalArgumentException("Role is required");
            }
        }
    }
}
