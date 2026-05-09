package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Rate Limiting.
 *
 * <p>Responsabilidad única: registrar {@link RateLimitProperties} en el contexto de Spring
 * para que esté disponible como bean inyectable. La lógica de resolución de buckets
 * y la gestión de estado se delegan a {@link InMemoryBucketResolver}.</p>
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfig {
}

