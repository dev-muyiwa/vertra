package com.vertra.domain.port.out;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    void set(String key, String value, Duration ttlSeconds);

    Optional<String> get(String key);

    void delete(String key);
}
