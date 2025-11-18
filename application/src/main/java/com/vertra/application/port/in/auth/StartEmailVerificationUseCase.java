package com.vertra.application.port.in.auth;

import com.vertra.domain.vo.Inet6;

import java.util.regex.Pattern;

public interface StartEmailVerificationUseCase {

    Pattern REDIRECT_PATH_PATTERN = Pattern.compile(
            "^" +
                    "/[A-Za-z0-9._~!$&'()*+,;=:@%/-]*" +           // path
                    "(\\?[A-Za-z0-9._~!$&'()*+,;=:@%/?-]*)?" +     // optional query
                    "(#[A-Za-z0-9._~!$&'()*+,;=:@%/?-]*)?" +       // optional fragment
                    "$"
    );

    void execute(StartEmailVerificationCommand command);

    record StartEmailVerificationCommand(
            String redirectPath,
            Inet6 ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (redirectPath == null || redirectPath.isBlank()) {
                throw new IllegalArgumentException("Redirect path is required");
            }
            if (redirectPath.length() > 500) {
                throw new IllegalArgumentException("Redirect path is too long");
            }
            if (!REDIRECT_PATH_PATTERN.matcher(redirectPath).matches()) {
                throw new IllegalArgumentException("Invalid redirect path");
            }
        }
    }
}
