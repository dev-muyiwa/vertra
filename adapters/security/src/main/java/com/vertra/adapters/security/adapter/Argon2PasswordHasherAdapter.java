package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.PasswordHashingPort;
import com.vertra.domain.vo.HashedPassword;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Argon2PasswordHasherAdapter implements PasswordHashingPort {

    private final Argon2 argon2;

    public Argon2PasswordHasherAdapter() {
        this.argon2 = Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id,
                32,
                64
        );
    }

    @Override
    public boolean verify(String passwordHash, String storedHash) {
        try {
            return argon2.verify(storedHash, passwordHash.toCharArray());
        } catch (Exception e) {
            log.error("Password verification failed", e);
            return false;
        }
    }

    @Override
    public String hash(String password) {
        try {
            return argon2.hash(3, 65536, 4, password.toCharArray());
        } catch (Exception e) {
            log.error("Password hashing failed", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
