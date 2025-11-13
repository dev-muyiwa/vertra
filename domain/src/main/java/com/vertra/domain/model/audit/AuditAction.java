package com.vertra.domain.model.audit;

import lombok.Getter;

@Getter
public enum AuditAction {
    USER_CREATED("user.created"),
    USER_LOGIN("user.login"),
    USER_LOGIN_FAILED("user.login.failed"),
    USER_LOGOUT("user.logout"),
    USER_PASSWORD_CHANGED("user.password.changed"),

    ORG_CREATED("org.created"),
    ORG_UPDATED("org.updated"),
    ORG_DELETED("org.deleted"),
    ORG_MEMBER_ADDED("org.member.added"),
    ORG_MEMBER_REMOVED("org.member.removed"),
    ORG_MEMBER_ROLE_CHANGED("org.member.role.changed"),

    PROJECT_CREATED("project.created"),
    PROJECT_UPDATED("project.updated"),
    PROJECT_DELETED("project.deleted"),
    PROJECT_MEMBER_ADDED("project.member.added"),
    PROJECT_MEMBER_REMOVED("project.member.removed"),

    CONFIG_CREATED("config.created"),
    CONFIG_UPDATED("config.updated"),
    CONFIG_DELETED("config.deleted"),
    CONFIG_LOCKED("config.locked"),
    CONFIG_UNLOCKED("config.unlocked"),

    SECRET_CREATED("secret.created"),
    SECRET_READ("secret.read"),
    SECRET_UPDATED("secret.updated"),
    SECRET_DELETED("secret.deleted"),
    SECRET_ROTATED("secret.rotated"),

    TOKEN_CREATED("token.created"),
    TOKEN_USED("token.used"),
    TOKEN_REVOKED("token.revoked");

    private final String value;

    AuditAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

