package com.vertra.adapters.persistence.mapper;

import com.vertra.adapters.persistence.entity.OtpEntity;
import com.vertra.domain.model.user.Otp;
import org.springframework.stereotype.Component;

@Component
public class OtpMapper {

    public Otp toDomain(OtpEntity entity) {
        if (entity == null) {
            return null;
        }

        return Otp.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .usedAt(entity.getUsedAt())
                .build();
    }

    public OtpEntity toEntity(Otp domain) {
        if (domain == null) {
            return null;
        }

        return OtpEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .createdAt(domain.getCreatedAt())
                .usedAt(domain.getUsedAt())
                .build();
    }
}
