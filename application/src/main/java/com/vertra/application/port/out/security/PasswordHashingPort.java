package com.vertra.application.port.out.security;

public interface PasswordHashingPort {
    boolean verify(String password, String storedHash);

    String hash(String password);
}
