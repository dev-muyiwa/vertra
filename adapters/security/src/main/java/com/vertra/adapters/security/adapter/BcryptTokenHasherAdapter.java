package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.vo.HashedToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BcryptTokenHasherAdapter implements TokenHashingPort {

    private final BCryptPasswordEncoder encoder;

    public BcryptTokenHasherAdapter() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public HashedToken hash(String token) {
        try {
            String hashedValue = encoder.encode(token);
            return new HashedToken(hashedValue);
        } catch (Exception e) {
            log.error("Token hashing failed", e);
            throw new RuntimeException("Token hashing failed", e);
        }
    }

    @Override
    public boolean verify(HashedToken token, HashedToken hash) {
        try {
            return encoder.matches(token.value(), hash.value());
        } catch (Exception e) {
            log.error("Token verification failed", e);
            return false;
        }
    }
}
