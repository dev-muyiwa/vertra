package com.vertra.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration class that sets up a RedisTemplate bean for interacting with Redis.
 * This configuration ensures that both keys and values are serialized as strings.
 * It uses the provided RedisConnectionFactory to establish connections to the Redis server.
 *
 * @version 1.0
 * @since 2024-06-10
 */
@Configuration
public class RedisConfig {

    /**
     * Creates and configures a RedisTemplate bean for String key-value pairs.
     *
     * @param factory the RedisConnectionFactory used to create connections to the Redis server
     * @return a configured RedisTemplate instance
     * @since 2024-06-10
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }
}
