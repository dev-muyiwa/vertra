package com.vertra.domain.model.token;

import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder(toBuilder = true)
public record ServiceToken(
        Uuid id,
        String name,
        String description,
        HashedToken hash,
        byte[] pubKey,
        byte[] encryptedPrivateKey,
        byte[] privateKeyIv,
        int usageCount,
        Set<Inet6> ipWhitelists,

        Uuid configId,
        Uuid createdBy,
        Uuid revokedBy,

        Instant lastUsedAt,
        Instant createdAt,
        Instant revokedAt,
        Instant expiresAt

) {
}
