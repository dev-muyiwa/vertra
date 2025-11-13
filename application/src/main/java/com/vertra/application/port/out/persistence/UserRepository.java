package com.vertra.application.port.out.persistence;

import com.vertra.domain.model.user.User;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Uuid;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findUndeletedById(Uuid id);

    Optional<User> findUndeletedByEmail(Email email);

    boolean existsByEmail(Email email);

    void delete(Uuid id);
}
