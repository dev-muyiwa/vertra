package com.vertra.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid credentials provided.");
    }

    public static UnauthorizedException accountInactive() {
        return new UnauthorizedException("Account is inactive.");
    }
}
