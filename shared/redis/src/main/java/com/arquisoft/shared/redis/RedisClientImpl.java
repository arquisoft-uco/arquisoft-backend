package com.arquisoft.shared.redis;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisClientImpl implements RedisClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return type.isInstance(value) ? (T) value : null;
    }

    @Override
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public long increment(String key) {
        Long result = stringRedisTemplate.opsForValue().increment(key);
        return !UtilObjeto.esNulo(result) ? result : 0L;
    }
}
