package com.vertra.application.service.organization;

import com.vertra.application.port.in.organization.GetOrganizationUseCase;
import com.vertra.application.port.out.persistence.OrganizationMemberRepository;
import com.vertra.application.port.out.persistence.OrganizationRepository;
import com.vertra.domain.exception.ForbiddenException;
import com.vertra.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOrganizationService implements GetOrganizationUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public OrganizationResponse execute(GetOrganizationQuery query) {
        query.validate();

        var organization = organizationRepository.findUndeletedById(query.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        var membership = organizationMemberRepository.findByUserIdAndOrgId(
                query.currentUserId(),
                query.organizationId()
        );

        if (membership.isEmpty() || !membership.get().isActive()) {
            throw new ForbiddenException("You do not have access to this organization");
        }

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug()
        );
    }
}
