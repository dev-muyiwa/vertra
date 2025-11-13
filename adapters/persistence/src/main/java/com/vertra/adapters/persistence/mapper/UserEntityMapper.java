package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.domain.model.user.User;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.HashedPassword;
import com.vertra.domain.vo.Uuid;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(String.valueOf(user.getEmail()))
                .emailVerifiedAt(user.getEmailVerifiedAt()) // change to isVerified on the domain model
                .passwordHash(String.valueOf(user.getPasswordHash()))
//                .publicKey(user.getPublicKey())
//                .encryptedPrivateKey(user.getEncryptedPrivateKey())
//                .privateKeyIv(user.getPrivateKeyIv())
//                .keyDerivationSalt(user.getKeyDerivationSalt())
//                .active(user.isActive())
                .lockedUntil(user.getLockedUntil())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .emailVerifiedAt(entity.getEmailVerifiedAt())
                .passwordHash(entity.getPasswordHash())
//                .active(entity.isActive())
                .lockedUntil(entity.getLockedUntil())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
