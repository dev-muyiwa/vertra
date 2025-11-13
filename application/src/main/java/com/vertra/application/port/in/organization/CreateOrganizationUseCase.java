package com.vertra.application.port.in.organization;

import com.vertra.domain.vo.Uuid;

public interface CreateOrganizationUseCase {

    OrganizationResponse execute(CreateOrganizationCommand command);

    record CreateOrganizationCommand(
            String name,
            String slug,
            Uuid currentUserId
    ) {
        public void validate() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Organization name is required");
            }
            if (slug == null || slug.isBlank()) {
                throw new IllegalArgumentException("Slug is required");
            }
            if (currentUserId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }

    record OrganizationResponse(
            Uuid id,
            String name,
            String slug
    ) {}
}
