package com.vertra.adapters.persistence.repository;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.application.port.out.persistence.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<UserEntity> findUndeletedByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<UserEntity> findUndeletedById(@Param("id") UUID id);

//    delete a user by setting deletedAt timestamp and return boolean indicating success
    @Modifying
    @Query("UPDATE UserEntity u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id AND u.deletedAt IS NULL")
    int delete(@Param("id") UUID id);

}
