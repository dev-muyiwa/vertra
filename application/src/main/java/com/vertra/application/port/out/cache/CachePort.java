package com.vertra.application.port.out.cache;

import java.time.Duration;
import java.util.Optional;

public interface CachePort {
    <T> void put(String key, T value, Duration ttl);

    <T> Optional<T> get(String key, Class<T> type);

    void delete(String key);

    void clear(String pattern);
}
