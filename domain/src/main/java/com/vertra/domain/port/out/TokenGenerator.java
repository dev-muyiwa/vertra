package com.vertra.domain.port.out;

import com.vertra.domain.vo.Uuid;

public interface TokenGenerator {
    String generateAccessToken(Uuid userId);

    String generateRefreshToken(Uuid userId);

    String generatePasswordResetToken(Uuid userId);

    Uuid validateToken(String token);
}
