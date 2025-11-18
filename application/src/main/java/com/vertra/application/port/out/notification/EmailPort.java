package com.vertra.application.port.out.notification;

import java.time.Duration;

public interface EmailPort {

    void sendVerificationEmail(
            String firstName,
            String toEmail,
            String verificationToken,
            String redirectPath,
            Duration expirationDuration
    );

    void sendVerificationSuccessEmail(
            String firstName,
            String toEmail
    );

    void sendPasswordResetEmail(
            String toEmail,
            String resetToken,
            String redirectPath
    );

}
