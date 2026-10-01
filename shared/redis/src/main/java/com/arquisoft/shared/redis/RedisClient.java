package com.arquisoft.shared.redis;

public interface RedisClient {
    <T> T get(String key, Class<T> type);

    void set(String key, Object value, long ttlSeconds);

    void delete(String key);

    boolean exists(String key);

    long increment(String key);
}
