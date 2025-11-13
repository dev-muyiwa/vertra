package com.vertra.domain.exception;

public class InvalidKeyException extends DomainException {
    public InvalidKeyException(String message) {
        super(message);
    }

    public static InvalidKeyException emptyKey() {
        return new InvalidKeyException("Secret key cannot be empty");
    }

    public static InvalidKeyException invalidFormat() {
        return new InvalidKeyException(
                "Secret key must be uppercase alphanumeric with underscores (e.g., DATABASE_URL, API_KEY)"
        );
    }

    public static InvalidKeyException tooLong() {
        return new InvalidKeyException("Secret key too long (max 255 characters)");
    }

    public static InvalidKeyException reserved(String key) {
        return new InvalidKeyException("Key '" + key + "' is reserved");
    }

    public static InvalidKeyException duplicateKey(String key) {
        return new InvalidKeyException("Secret with key '" + key + "' already exists");
    }
}
