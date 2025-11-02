package com.vertra.domain.port.out;

import com.vertra.domain.vo.HashedPassword;

public interface PasswordHasher {
    HashedPassword hash(String plain);

    boolean verify(String plain, HashedPassword hash);
}
