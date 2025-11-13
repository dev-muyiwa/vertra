package com.vertra.domain.exception;

public class SecretEncryptionException extends DomainException {
    public SecretEncryptionException(String message) {
        super(message);
    }

  public SecretEncryptionException(String message, Throwable cause) {
    super(message, cause);
  }

  public static SecretEncryptionException encryptionFailed(String reason) {
    return new SecretEncryptionException("Encryption failed: " + reason);
  }

  public static SecretEncryptionException decryptionFailed(String reason) {
    return new SecretEncryptionException("Decryption failed: " + reason);
  }

  public static SecretEncryptionException invalidCiphertext() {
    return new SecretEncryptionException("Invalid ciphertext or corrupted data");
  }

  public static SecretEncryptionException authenticationFailed() {
    return new SecretEncryptionException("GCM authentication failed - data may be tampered");
  }
}
