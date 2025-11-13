package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.RegisterUserUseCase;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.security.PasswordHashingPort;
import com.vertra.domain.exception.DuplicateResourceException;
import com.vertra.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepo;
    private final PasswordHashingPort passwordHasher;
//    private final AuditPort auditPort;

    @Override
    @Transactional
    public RegisterUserResponse execute(RegisterUserCommand cmd) {
        cmd.validate();

        if (userRepo.existsByEmail(cmd.email())) {
            throw DuplicateResourceException.email(cmd.email());
        }

        User userObj = User.builder()
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .email(cmd.email().value())
                .passwordHash(passwordHasher.hash(cmd.password()))
                .build();


        User savedUser = userRepo.save(userObj);

//        auditPort.log(
//                AuditAction.USER_CREATED,
//                null,
//                savedUser.getId(),
//                null,
//                ActorType.USER,
//                null, null, null,
//                null, null, null, null, true, null
//        );

        log.info("Registered new user with ID: {}", savedUser.getId());

        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail()
        );
    }
}
