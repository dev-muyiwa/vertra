package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.vo.HashedToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Component
@Primary
public class Sha256TokenHasherAdapter implements TokenHashingPort {

    private final MessageDigest digest;

    public Sha256TokenHasherAdapter() {
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public HashedToken hash(String token) {
        try {
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            String hexHash = HexFormat.of().formatHex(hashBytes);
            return new HashedToken(hexHash);
        } catch (Exception e) {
            log.error("Token hashing failed", e);
            throw new RuntimeException("Token hashing failed", e);
        }
    }

    @Override
    public boolean verify(HashedToken token, HashedToken hash) {
        try {
            HashedToken computedHash = hash(token.value());
            return computedHash.value().equals(hash.value());
        } catch (Exception e) {
            log.error("Token verification failed", e);
            return false;
        }
    }
}
