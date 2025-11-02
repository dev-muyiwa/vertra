package com.vertra.domain.service;

import com.vertra.domain.exception.InvalidCredentialsException;
import com.vertra.domain.model.user.User;
import com.vertra.domain.port.out.CacheService;
import com.vertra.domain.port.out.PasswordHasher;
import com.vertra.domain.port.out.TokenGenerator;
import com.vertra.domain.port.out.IUserRepository;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Uuid;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class AuthenticationService {

    private final IUserRepository userRepo;
    private final PasswordHasher hasher;
    private final TokenGenerator tokenGen;
    private final CacheService cache;

    public User register(String email, String plainPassword) {
        var hashedPassword = hasher.hash(plainPassword);
        var user = User.builder()
                .id(Uuid.random())
                .email(new Email(email))
                .passwordHash(hashedPassword)
                .build();
        return userRepo.save(user);
    }

    public AuthTokens login(String email, String password) {
        var user = userRepo.findByEmail(new Email(email))
                .orElseThrow(InvalidCredentialsException::new);

        if (!hasher.verify(password, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        var accessToken = tokenGen.generateAccessToken(user.id());
        var refreshToken = tokenGen.generateRefreshToken(user.id());
        var expiresIn = Duration.ofMinutes(15);

        cache.set("access:" + user.id().value(), refreshToken, expiresIn);

        return new AuthTokens(accessToken, refreshToken, expiresIn.getSeconds());
    }

    public record AuthTokens(String accessToken, String refreshToken, long expiresIn) {
    }
}
