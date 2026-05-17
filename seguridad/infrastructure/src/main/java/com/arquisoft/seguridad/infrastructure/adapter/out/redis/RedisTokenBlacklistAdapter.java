package com.arquisoft.seguridad.infrastructure.adapter.out.redis;

import com.arquisoft.seguridad.application.auth.port.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistAdapter implements TokenBlacklistPort {

    private static final String PREFIX = "arquisoft:blacklist:jti:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void invalidarToken(String jti, long ttlSegundos) {
        // SET arquisoft:blacklist:jti:{jti} 1 EX {ttl}
        // TTL = vida restante del token. Redis expira la entrada automaticamente
        // cuando el token habria caducado de todas formas — sin acumulacion innecesaria.
        stringRedisTemplate.opsForValue()
                .set(PREFIX + jti, "1", Duration.ofSeconds(ttlSegundos));
    }

    @Override
    public boolean estaInvalidado(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + jti));
    }
}
