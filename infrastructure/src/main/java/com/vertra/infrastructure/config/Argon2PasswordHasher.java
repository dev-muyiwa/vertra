package com.vertra.infrastructure.config;

import com.vertra.domain.port.out.PasswordHasher;
import com.vertra.domain.vo.HashedPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Argon2PasswordHasher implements PasswordHasher {
    @Override
    public HashedPassword hash(String plain) {
        return null;
    }

    @Override
    public boolean verify(String plain, HashedPassword hash) {
        return false;
    }
}
