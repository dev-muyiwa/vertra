package com.vertra.domain.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() {
        super("Invalid or expired token");
    }
}
