package com.vertra.application.port.in.organization;

import com.vertra.domain.vo.Uuid;

import java.util.List;

public interface ListOrganizationsUseCase {

    List<OrganizationSummary> execute(ListOrganizationsQuery query);

    record ListOrganizationsQuery(
            Uuid currentUserId
    ) {
        public void validate() {
            if (currentUserId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }

    record OrganizationSummary(
            Uuid id,
            String name,
            String slug,
            String role
    ) {
    }
}
