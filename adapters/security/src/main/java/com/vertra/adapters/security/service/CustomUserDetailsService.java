package com.vertra.adapters.security.service;

import com.vertra.adapters.security.UserPrincipal;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.domain.model.user.User;
import com.vertra.domain.vo.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepo.findUndeletedByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

//        if (!user.isActive()) {
//            throw new UsernameNotFoundException("User account is inactive: " + email);
//        }

//        if (user.isDeleted()) {
//            throw new UsernameNotFoundException("User account has been deleted: " + email);
//        }

        return UserPrincipal.from(user);
    }
}
