package com.vertra.adapters.persistence.adapter;

import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionRepositoryAdapter implements UserSessionRepository {
    @Override
    public UserSession save(UserSession session) {
        return null;
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
        return Optional.empty();
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
    public void revokeAllForUser(Uuid userId) {

    }

    @Override
    public void deleteExpiredSessions() {

    }
}
