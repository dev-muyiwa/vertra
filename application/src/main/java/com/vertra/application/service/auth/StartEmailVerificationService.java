package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.StartEmailVerificationUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.notification.EmailPort;
import com.vertra.application.port.out.persistence.OtpRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.security.SecurityContextPort;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.domain.exception.ResourceNotFoundException;
import com.vertra.domain.exception.ValidationException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.Otp;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartEmailVerificationService implements StartEmailVerificationUseCase {

    private final UserRepository userRepo;
    private final OtpRepository tokenRepo;
    private final TokenGenerationPort tokenGen;
    private final SecurityContextPort security;
    private final EmailPort emailPort;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public void execute(StartEmailVerificationCommand command) {
        Uuid currentUserId = new Uuid(security.getCurrentUserId());
        log.info("Starting email verification for user ID: {}", currentUserId);
        command.validate();

        var user = userRepo.findUndeletedById(currentUserId)
                .orElseThrow(() -> ResourceNotFoundException.user(currentUserId.value()));

        if (user.getEmailVerifiedAt() != null) {
            throw new ValidationException("Email already verified");
        }

        String token = tokenGen.generateToken();

        Duration expiryDuration = Duration.ofMinutes(15);

        Otp otp = Otp.create(
                user.getId(),
                token,
                expiryDuration
        );

        tokenRepo.save(otp);

        try {
            emailPort.sendVerificationEmail(
                    user.getFirstName(),
                    user.getEmail(),
                    token,
                    command.redirectPath(),
                    expiryDuration
            );
            log.info("Sent email verification to {}", user.getEmail());
            auditPort.log(
                    AuditAction.EMAIL_VERIFICATION_STARTED,
                    null,
                    user.getId(),
                    null,
                    ActorType.USER,
                    null,
                    null,
                    null,
                    command.ipAddress(),
                    command.userAgent(),
                    UUID.randomUUID(),
                    null,
                    true,
                    "Email verification started for user ID: " + user.getId()
            );
        } catch (Exception e) {
            log.error("Failed to send email verification to {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send email verification", e);
        }
    }
}
