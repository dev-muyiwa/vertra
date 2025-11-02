package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.domain.port.out.IUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, IUserRepository {
    Optional<UserEntity> findByEmail(String email);
}
