package com.vertra.adapters.security.filter;

import com.vertra.adapters.security.UserPrincipal;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.domain.exception.ResourceNotFoundException;
import com.vertra.domain.vo.Uuid;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerationPort tokenGen;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt == null) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            TokenGenerationPort.TokenClaims claims = tokenGen.parseToken(jwt);

            if (claims.valid() && claims.userId() != null) {
                var user = userRepository.findUndeletedById(new Uuid(claims.userId()))
                        .orElseThrow(() -> ResourceNotFoundException.user(claims.userId()));

                if (user.isLocked()) {
                    log.warn("Account is locked for user: {}", claims.userId());
                    filterChain.doFilter(request, response);
                    return;
                }

                UserPrincipal userPrincipal = UserPrincipal.from(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("User authenticated via JWT: {}", claims.jti());
            } else {
                log.warn("Invalid JWT token");
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
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
