package com.vertra.application.service.organization;

import com.vertra.application.port.in.organization.CreateOrganizationUseCase;
import com.vertra.application.port.out.persistence.OrganizationMemberRepository;
import com.vertra.application.port.out.persistence.OrganizationRepository;
import com.vertra.domain.exception.ConflictException;
import com.vertra.domain.model.organization.Organization;
import com.vertra.domain.model.organization.OrganizationMember;
import com.vertra.domain.model.organization.OrganizationRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateOrganizationService implements CreateOrganizationUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    @Transactional
    public OrganizationResponse execute(CreateOrganizationCommand command) {
        command.validate();

        String slug = generateSlug(command.name());

        if (organizationRepository.existsBySlug(slug)) {
            throw new ConflictException("An organization with this name already exists");
        }

        var organization = Organization.builder()
                .name(command.name())
                .slug(slug)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        organization.validateName();
        organization.validateSlug();

        var savedOrg = organizationRepository.save(organization);

        var member = OrganizationMember.builder()
                .role(OrganizationRole.OWNER)
                .userId(command.currentUserId())
                .orgId(savedOrg.getId())
                .joinedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        organizationMemberRepository.save(member);

        return new OrganizationResponse(
                savedOrg.getId(),
                savedOrg.getName(),
                savedOrg.getSlug()
        );
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
