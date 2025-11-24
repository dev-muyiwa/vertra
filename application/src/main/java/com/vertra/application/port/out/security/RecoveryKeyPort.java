package com.vertra.application.port.out.security;

public interface RecoveryKeyPort {
    String generateRecoverySalt();

    String hashRecoveryKey(String recoveryKey, String salt);

    boolean verifyRecoveryKey(String recoveryKey, String salt, String storedHash);
}
