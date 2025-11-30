package com.vertra.adapters.web.dto.response.organization;

import java.util.UUID;

public record OrganizationSummaryResponse(
        UUID id,
        String name,
        String slug,
        String role
) {
}
