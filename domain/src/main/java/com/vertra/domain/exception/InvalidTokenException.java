package com.vertra.domain.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super("Invalid or expired token");
    }

    private InvalidTokenException(String message) {
        super(message);
    }

    public static InvalidTokenException expired() {
        return new InvalidTokenException("Verification token has expired");
    }

    public static InvalidTokenException invalid() {
        return new InvalidTokenException("Invalid verification token");
    }

    public static InvalidTokenException alreadyUsed() {
        return new InvalidTokenException("Verification token has already been used");
    }
}
