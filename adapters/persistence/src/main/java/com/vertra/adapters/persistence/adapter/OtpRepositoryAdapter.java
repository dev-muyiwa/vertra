package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.entity.OtpEntity;
import com.vertra.adapters.persistence.mapper.OtpMapper;
import com.vertra.adapters.persistence.repository.JpaOtpRepository;
import com.vertra.application.port.out.persistence.OtpRepository;
import com.vertra.domain.model.user.Otp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpRepositoryAdapter implements OtpRepository {

    private final JpaOtpRepository jpaRepo;
    private final OtpMapper mapper;


    @Override
    public Otp save(Otp token) {
        OtpEntity entity = mapper.toEntity(token);
        OtpEntity savedEntity = jpaRepo.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Otp> findByToken(String token, UUID userId) {
        Optional<OtpEntity> entityOpt = jpaRepo.findByToken(token, userId);
        return entityOpt.map(mapper::toDomain);
    }

    @Override
    public Optional<Otp> findLatestByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepo.deleteExpiredTokens(Instant.now());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepo.deleteByUserId(userId);
    }
}
