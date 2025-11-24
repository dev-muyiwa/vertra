package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.UserEntity;
import com.vertra.domain.model.user.OAuthProvider;
import com.vertra.domain.model.user.User;
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
                .email(user.getEmail())
                .oAuthProvider(toEntityProvider(user.getOAuthProvider()))
                .oAuthId(user.getOAuthId())
                .profilePictureUrl(user.getProfilePictureUrl())
                .accountPublicKey(user.getAccountPublicKey())
                .recoveryEncryptedPrivateKey(user.getRecoveryEncryptedPrivateKey())
                .recoverySalt(user.getRecoverySalt())
                .emailVerifiedAt(user.getEmailVerifiedAt())
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
                .oAuthProvider(toDomainProvider(entity.getOAuthProvider()))
                .oAuthId(entity.getOAuthId())
                .profilePictureUrl(entity.getProfilePictureUrl())
                .accountPublicKey(entity.getAccountPublicKey())
                .recoveryEncryptedPrivateKey(entity.getRecoveryEncryptedPrivateKey())
                .recoverySalt(entity.getRecoverySalt())
                .emailVerifiedAt(entity.getEmailVerifiedAt())
                .lockedUntil(entity.getLockedUntil())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity.OAuthProviderEntity toEntityProvider(OAuthProvider provider) {
        if (provider == null) {
            return null;
        }
        return switch (provider) {
            case GOOGLE -> UserEntity.OAuthProviderEntity.GOOGLE;
            case GITHUB -> UserEntity.OAuthProviderEntity.GITHUB;
            case MICROSOFT -> UserEntity.OAuthProviderEntity.MICROSOFT;
        };
    }

    private OAuthProvider toDomainProvider(UserEntity.OAuthProviderEntity provider) {
        if (provider == null) {
            return null;
        }
        return switch (provider) {
            case GOOGLE -> OAuthProvider.GOOGLE;
            case GITHUB -> OAuthProvider.GITHUB;
            case MICROSOFT -> OAuthProvider.MICROSOFT;
        };
    }
}
