package com.vertra.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid email or password");
    }

    public static UnauthorizedException accountLocked() {
        return new UnauthorizedException("Account is locked due to multiple failed login attempts");
    }

    public static UnauthorizedException sessionExpired() {
        return new UnauthorizedException("Session has expired");
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException("Invalid or expired token");
    }

    public static UnauthorizedException accountInactive() {
        return new UnauthorizedException("Account is inactive");
    }

    public static UnauthorizedException accountDeactivated() {
        return new UnauthorizedException("Account has been deactivated");
    }

    public static UnauthorizedException notAuthenticated() {
        return new UnauthorizedException("User not authenticated");
    }
}
