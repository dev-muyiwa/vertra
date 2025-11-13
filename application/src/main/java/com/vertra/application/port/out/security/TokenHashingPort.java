package com.vertra.application.port.out.security;

import com.vertra.domain.vo.HashedToken;

public interface TokenHashingPort {
    HashedToken hash(String token);

    boolean verify(HashedToken token, HashedToken hash);
}
