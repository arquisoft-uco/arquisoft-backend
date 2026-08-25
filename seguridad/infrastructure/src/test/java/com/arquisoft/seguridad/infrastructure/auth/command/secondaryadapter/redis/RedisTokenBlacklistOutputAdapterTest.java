package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.redis;

import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisTokenBlacklistOutputAdapterTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RedisTokenBlacklistOutputAdapter adapter;

    @Test
    void debeInvalidarToken_cuandoJtiYTtlValidos() {
        // Arrange
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        // Act
        adapter.invalidarToken("jti-12345", 3600L);

        // Assert
        verify(valueOperations).set(keyCaptor.capture(), eq("1"), durationCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo("arquisoft:blacklist:jti:jti-12345");
        assertThat(durationCaptor.getValue()).isEqualTo(Duration.ofSeconds(3600L));
    }

    @Test
    void debeRetornarTrue_cuandoTokenEstaEnBlacklist() {
        // Arrange
        when(stringRedisTemplate.hasKey("arquisoft:blacklist:jti:jti-revocado")).thenReturn(true);

        // Act
        boolean estaInvalidado = adapter.estaInvalidado("jti-revocado");

        // Assert
        assertThat(estaInvalidado).isTrue();
        verify(stringRedisTemplate).hasKey("arquisoft:blacklist:jti:jti-revocado");
    }

    @Test
    void debeRetornarFalso_cuandoTokenNoEstaEnBlacklist() {
        // Arrange
        when(stringRedisTemplate.hasKey("arquisoft:blacklist:jti:jti-activo")).thenReturn(false);

        // Act
        boolean estaInvalidado = adapter.estaInvalidado("jti-activo");

        // Assert
        assertThat(estaInvalidado).isFalse();
    }

    @Test
    void debeRetornarFalso_cuandoHasKeyRetornaNull() {
        // Arrange - Redis puede retornar null si hay problema de conexión
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(null);

        // Act
        boolean estaInvalidado = adapter.estaInvalidado("jti-cualquiera");

        // Assert
        assertThat(estaInvalidado).isFalse();
    }
}
