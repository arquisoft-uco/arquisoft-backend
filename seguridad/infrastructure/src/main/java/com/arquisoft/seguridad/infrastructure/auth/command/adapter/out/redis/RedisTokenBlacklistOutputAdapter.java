package com.arquisoft.seguridad.infrastructure.auth.command.adapter.out.redis;

import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistOutputAdapter implements TokenBlacklistOutputPort {

    private static final String PREFIX = "arquisoft:blacklist:jti:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void invalidarToken(String jti, long ttlSegundos) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX + jti, "1", Duration.ofSeconds(ttlSegundos));
    }

    @Override
    public boolean estaInvalidado(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + jti));
    }
}
