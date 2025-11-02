package com.vertra.domain.exception;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException("You do not have sufficient permissions to perform this action.");
    }

    public static ForbiddenException notOrganizationMember() {
        return new ForbiddenException("You do not have access to this organization.");
    }

    public static ForbiddenException notProjectMember() {
        return new ForbiddenException("You do not have access to this project.");
    }

    public static ForbiddenException notConfigMember() {
        return new ForbiddenException("You do not have access to this configuration.");
    }
}
