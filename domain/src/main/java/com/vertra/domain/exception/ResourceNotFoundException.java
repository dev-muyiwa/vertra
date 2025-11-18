package com.vertra.domain.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException user(UUID identifier) {
        return new ResourceNotFoundException("User not found: " + identifier);
    }

    public static ResourceNotFoundException organization(String identifier) {
        return new ResourceNotFoundException("Organization not found: " + identifier);
    }

    public static ResourceNotFoundException project(String identifier) {
        return new ResourceNotFoundException("Project not found: " + identifier);
    }

    public static ResourceNotFoundException config(String identifier) {
        return new ResourceNotFoundException("Config not found: " + identifier);
    }

    public static ResourceNotFoundException secret(String identifier) {
        return new ResourceNotFoundException("Secret not found: " + identifier);
    }

    public static ResourceNotFoundException serviceToken(String identifier) {
        return new ResourceNotFoundException("Service token not found: " + identifier);
    }
}
