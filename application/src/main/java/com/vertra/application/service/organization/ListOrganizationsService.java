package com.vertra.application.service.organization;

import com.vertra.application.port.in.organization.ListOrganizationsUseCase;
import com.vertra.application.port.out.persistence.OrganizationMemberRepository;
import com.vertra.application.port.out.persistence.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListOrganizationsService implements ListOrganizationsUseCase {

    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public List<OrganizationSummary> execute(ListOrganizationsQuery query) {
        query.validate();

        var memberships = organizationMemberRepository.findActiveByUserId(query.currentUserId());

        if (memberships.isEmpty()) {
            return List.of();
        }

        var orgIds = memberships.stream()
                .map(m -> m.getOrgId())
                .toList();

        var organizations = organizationRepository.findAllByIdIn(orgIds);

        return memberships.stream()
                .flatMap(membership -> organizations.stream()
                        .filter(org -> org.getId().equals(membership.getOrgId()))
                        .map(org -> new OrganizationSummary(
                                org.getId(),
                                org.getName(),
                                org.getSlug(),
                                membership.getRole().name()
                        ))
                )
                .toList();
    }
}
