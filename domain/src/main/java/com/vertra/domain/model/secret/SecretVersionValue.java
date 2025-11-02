package com.vertra.domain.model.secret;


import com.vertra.domain.vo.EncryptedValue;
import com.vertra.domain.vo.PublicKeyFingerPrint;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record SecretVersionValue(
        Uuid id,
        EncryptedValue encryptedValue,
        byte[] encryptedSymKey,
        PublicKeyFingerPrint publicKeyFingerprint,

        Uuid memberId,
        Uuid secretId,

        Instant createdAt
) {
}
