package com.vertra.adapters.security.filter;

import com.vertra.adapters.security.UserPrincipal;
import com.vertra.application.port.out.security.DatabaseContextPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RLSContextFilter extends OncePerRequestFilter {

    private final DatabaseContextPort dbCtx;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                if (principal instanceof UserPrincipal userPrincipal) {
                    dbCtx.setUserContext(userPrincipal.getId());
                    log.debug("RLS context set for user: {}", userPrincipal.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error setting RLS context", e);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                dbCtx.clearUserContext();
            } catch (Exception e) {
                log.warn("Error clearing RLS context", e);
            }
        }
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//
//        return path.equals("/auth/register") ||
//                path.equals("/auth/login") ||
//                path.startsWith("/actuator/health") ||
//                path.startsWith("/swagger-ui") ||
//                path.startsWith("/v3/api-docs");
//    }
}
