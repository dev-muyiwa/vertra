package com.vertra.infrastructure.adapter;

import com.vertra.application.port.out.notification.EmailPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class MockEmailAdapter implements EmailPort {
    @Override
    public void sendVerificationEmail(String firstName, String toEmail, String verificationToken, String redirectPath, Duration expirationDuration) {
        log.info("=".repeat(80));
        log.info("EMAIL VERIFICATION");
        log.info("To: {}", toEmail);
        log.info("Verification Link: http://localhost:3000/verify-email?token={}&redirect={}",
                verificationToken, redirectPath);
        log.info("Token: {}", verificationToken);
        log.info("Redirect Path: {}", redirectPath);
        log.info("=".repeat(80));
    }

    @Override
    public void sendVerificationSuccessEmail(String firstName, String toEmail) {
        log.info("=".repeat(80));
        log.info("EMAIL VERIFICATION SUCCESS");
        log.info("To: {}", toEmail);
        log.info("Hello {}, your email has been successfully verified!", firstName);
        log.info("=".repeat(80));
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken, String redirectPath) {
        log.info("=".repeat(80));
        log.info("PASSWORD RESET");
        log.info("To: {}", toEmail);
        log.info("Password Reset Link: http://localhost:3000/reset-password?token={}&redirect={}",
                resetToken, redirectPath);
        log.info("Token: {}", resetToken);
        log.info("Redirect Path: {}", redirectPath);
        log.info("=".repeat(80));
    }
}
