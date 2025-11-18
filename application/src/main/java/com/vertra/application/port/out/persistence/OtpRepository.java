package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.user.Otp;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository {

    Otp save(Otp token);

    Optional<Otp> findByToken(String token, UUID userId);

    Optional<Otp> findLatestByUserId(UUID userId);

    void deleteExpiredTokens();

    void deleteByUserId(UUID userId);
}
