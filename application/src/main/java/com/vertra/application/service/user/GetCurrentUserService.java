package com.vertra.application.service.user;

import com.vertra.application.port.in.user.GetCurrentUserUseCase;
import com.vertra.application.port.out.persistence.OrganizationMemberRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public UserInfo execute(GetCurrentUserQuery query) {
        query.validate();

        var user = userRepository.findUndeletedById(query.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean hasOrganization = organizationMemberRepository.existsActiveByUserId(query.userId());

        return new UserInfo(
                query.userId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                hasOrganization,
                user.isEmailVerified()
        );
    }
}
