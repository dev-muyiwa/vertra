package com.vertra.adapters.security.provider;

import com.vertra.adapters.security.UserPrincipal;
import com.vertra.adapters.security.service.CustomUserDetailsService;
import com.vertra.application.port.out.security.PasswordHashingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VertraAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordHashingPort passwordHasher;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("Authenticating user: {}", email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (userDetails instanceof UserPrincipal userPrincipal) {
            if (!passwordHasher.verify(password, userPrincipal.getPassword())) {
                log.warn("Authentication failed for user: {}", email);
                throw new BadCredentialsException("Invalid credentials");
            }

            log.info("User authenticated successfully: {}", email);

            return new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    password,
                    userPrincipal.getAuthorities()
            );
        }

        throw new BadCredentialsException("Invalid user details");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
