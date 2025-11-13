package com.vertra.domain.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class RateLimitExceededException extends DomainException {
  private final int limit;
  private final Instant resetAt;

  public RateLimitExceededException(String message, int limit, Instant resetAt) {
    super(message);
    this.limit = limit;
    this.resetAt = resetAt;
  }

  public static RateLimitExceededException forUser(int limit, Instant resetAt) {
    return new RateLimitExceededException(
            "Rate limit exceeded. Limit: " + limit + " requests per hour",
            limit,
            resetAt
    );
  }

  public static RateLimitExceededException forToken(int limit, Instant resetAt) {
    return new RateLimitExceededException(
            "Service token rate limit exceeded. Limit: " + limit + " requests per hour",
            limit,
            resetAt
    );
  }

  public static RateLimitExceededException forIp(int limit, Instant resetAt) {
    return new RateLimitExceededException(
            "IP address rate limit exceeded. Limit: " + limit + " requests per hour",
            limit,
            resetAt
    );
  }

  public long getSecondsUntilReset() {
    return Instant.now().until(resetAt, java.time.temporal.ChronoUnit.SECONDS);
  }
}
