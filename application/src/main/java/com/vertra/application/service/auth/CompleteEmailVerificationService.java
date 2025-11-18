package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.VerifyEmailUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.notification.EmailPort;
import com.vertra.application.port.out.persistence.OtpRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.security.SecurityContextPort;
import com.vertra.domain.exception.InvalidTokenException;
import com.vertra.domain.exception.ResourceNotFoundException;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteEmailVerificationService implements VerifyEmailUseCase {

    private final UserRepository userRepo;
    private final OtpRepository tokenRepo;
    private final SecurityContextPort security;
    private final EmailPort emailPort;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public void execute(VerifyEmailCommand command) {
        Uuid currentUserId = new Uuid(security.getCurrentUserId());
        command.validate();

        var user = userRepo.findUndeletedById(currentUserId)
                .orElseThrow(() -> ResourceNotFoundException.user(currentUserId.value()));

        if (user.getEmailVerifiedAt() != null) {
            auditPort.log(
                    AuditAction.EMAIL_VERIFICATION_ATTEMPT, null, user.getId(), null, ActorType.USER,
                    null, null, null, command.ipAddress(), command.userAgent(), UUID.randomUUID(), null,
                    false, "Email already verified for user ID: " + currentUserId
            );
            return;
        }

        var otp = tokenRepo.findByToken(command.token(), currentUserId.value())
                .orElseThrow(InvalidTokenException::invalid);

        if (!otp.getUserId().equals(user.getId())) {
            auditPort.log(
                    AuditAction.EMAIL_VERIFICATION_FAILED, null, user.getId(), null, ActorType.USER,
                    null, null, null, command.ipAddress(), command.userAgent(), UUID.randomUUID(), Map.of("reason", "Token does not match user"),
                    false, "Invalid token for user ID: " + currentUserId
            );
            throw UnauthorizedException.invalidToken();
        }

        if (otp.isExpired()) {
            auditPort.log(
                    AuditAction.EMAIL_VERIFICATION_FAILED, null, user.getId(), null, ActorType.USER,
                    null, null, null, command.ipAddress(), command.userAgent(), UUID.randomUUID(), Map.of("reason", "Token expired"),
                    false, "Expired token for user ID: " + currentUserId
            );
            throw InvalidTokenException.expired();
        }

        user.markEmailAsVerified();
        userRepo.save(user);

        tokenRepo.deleteByUserId(user.getId());

        emailPort.sendVerificationSuccessEmail(user.getFirstName(), user.getEmail());

        auditPort.log(
                AuditAction.EMAIL_VERIFIED, null, user.getId(), null, ActorType.USER,
                null, null, null, command.ipAddress(), command.userAgent(), UUID.randomUUID(), null,
                true, "Email verified successfully for user ID: " + currentUserId
        );

        log.info("Email verified successfully for user ID: {}", currentUserId);
    }
}
