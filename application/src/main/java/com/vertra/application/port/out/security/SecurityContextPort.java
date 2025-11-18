package com.vertra.application.port.out.security;

import java.util.UUID;

public interface SecurityContextPort {
    UUID getCurrentUserId();

    boolean isAuthenticated();

    UUID getCurrentOrganizationId();
}
