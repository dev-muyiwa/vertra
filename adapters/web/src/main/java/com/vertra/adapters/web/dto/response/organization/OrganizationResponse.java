package com.vertra.adapters.web.dto.response.organization;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug
) {
}
