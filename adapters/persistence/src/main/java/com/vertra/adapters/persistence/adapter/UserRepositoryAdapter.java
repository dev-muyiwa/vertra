package com.vertra.adapters.persistence.adapter;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.adapters.persistence.mapper.UserEntityMapper;
import com.vertra.adapters.persistence.repository.JpaUserRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.domain.model.user.User;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepo;
    private final UserEntityMapper mapper;

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user.getId());

        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepo.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findUndeletedById(Uuid id) {
        log.debug("Finding undeleted user by id: {}", id);

        return jpaRepo.findUndeletedById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findUndeletedByEmail(Email email) {
        log.debug("Finding undeleted user by email: {}", email);

        return jpaRepo.findUndeletedByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        log.debug("Checking existence of user by email: {}", email);

        return jpaRepo.existsByEmail(email.value());
    }

    @Override
    public void delete(Uuid id) {
        log.debug("Deleting user by id: {}", id);

        int deleted = jpaRepo.delete(id.value());
        if (deleted == 0) {
            log.warn("No user deleted for id: {}", id);
        }
    }
}
