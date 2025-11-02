package com.vertra.domain.port.out;

import com.vertra.domain.model.user.User;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Uuid;

import java.util.Optional;

public interface IUserRepository {
    User save(User user);

    Optional<User> findById(Uuid id);

    Optional<User> findByEmail(Email email);
}
