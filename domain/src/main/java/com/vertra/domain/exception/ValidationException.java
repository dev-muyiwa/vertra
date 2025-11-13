package com.vertra.domain.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join(", ", errors));
        this.errors = new ArrayList<>(errors);
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public static ValidationException single(String error) {
        return new ValidationException(error);
    }

    public static ValidationException multiple(List<String> errors) {
        return new ValidationException(errors);
    }
}
