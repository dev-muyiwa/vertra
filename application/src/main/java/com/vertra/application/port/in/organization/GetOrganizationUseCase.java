package com.vertra.application.port.in.organization;

import com.vertra.domain.vo.Uuid;

public interface GetOrganizationUseCase {

    OrganizationResponse execute(GetOrganizationQuery query);

    record GetOrganizationQuery(
            Uuid organizationId,
            Uuid currentUserId
    ) {
        public void validate() {
            if (organizationId == null) {
                throw new IllegalArgumentException("Organization ID is required");
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
