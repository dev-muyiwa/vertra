package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.entity.UserSessionEntity;
import com.vertra.adapters.persistence.mapper.UserSessionEntityMapper;
import com.vertra.adapters.persistence.repository.JpaUserSessionRepository;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionRepositoryAdapter implements UserSessionRepository {

    private final JpaUserSessionRepository jpaRepo;
    private final UserSessionEntityMapper mapper;

    @Override
    public UserSession save(UserSession session) {
        log.debug("Saving user session {}", session.getId());

        UserSessionEntity entity = mapper.toEntity(session);
        UserSessionEntity savedEntity = jpaRepo.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserSession> findById(Uuid id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserSession> findByTokenHash(HashedToken tokenHash) {
        return Optional.empty();
    }

    @Override
    public Optional<UserSession> findByRefreshTokenHash(HashedToken refreshTokenHash) {
        return jpaRepo.findByRefreshTokenHash(refreshTokenHash.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByUserIdAndTokenHash(Uuid userId, HashedToken tokenHash) {
        return Optional.empty();
    }

    @Override
    public List<UserSession> findByUserId(Uuid userId) {
        return List.of();
    }

    @Override
    public List<UserSession> findActiveByUserIdAndDeviceId(Uuid userId, Uuid deviceId) {
        return jpaRepo.findActiveByUserIdAndDeviceId(userId.value(), deviceId.value(), Instant.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAllForUser(Uuid userId) {

    }

    @Override
    public void deleteExpiredSessions() {

    }
}
