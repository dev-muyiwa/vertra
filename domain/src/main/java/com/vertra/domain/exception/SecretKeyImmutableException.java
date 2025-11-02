package com.vertra.domain.exception;

public class SecretKeyImmutableException extends DomainException {
    public SecretKeyImmutableException() {
        super("Secret key cannot be modified after creation.");
    }
}
