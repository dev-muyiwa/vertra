package com.vertra.adapters.security.adapter;

import com.vertra.adapters.security.UserPrincipal;
import com.vertra.application.port.out.security.SecurityContextPort;
import com.vertra.domain.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class SecurityContextAdapter implements SecurityContextPort {
    @Override
    public UUID getCurrentUserId() {
        Authentication auth = getAuthenticationOrThrow();
        
        if (auth.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        
        // Fallback: try to parse from name (for backward compatibility)
        String userIdStr = auth.getName();
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            log.error("Invalid user ID format in security context: {}", userIdStr);
            throw new IllegalStateException("Invalid user ID format in security context", e);
        }
    }

    @Override
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return false;
        }

        if (!auth.isAuthenticated()) {
            return false;
        }

        return !"anonymousUser".equals(auth.getName());
    }

    @Override
    public UUID getCurrentOrganizationId() {
        return null;
    }

    private Authentication getAuthenticationOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw UnauthorizedException.notAuthenticated();
        }

        log.debug("Current user: {}", auth);

        if ("anonymousUser".equals(auth.getName())) {
            throw UnauthorizedException.notAuthenticated();
        }

        return auth;
    }
}
