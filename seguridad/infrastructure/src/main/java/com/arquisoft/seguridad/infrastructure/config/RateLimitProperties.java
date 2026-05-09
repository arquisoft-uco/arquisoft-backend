package com.arquisoft.seguridad.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para el rate limiting.
 *
 * <p>Centraliza el prefijo {@code security.rate-limit.*} en un record validado
 * en startup, eliminando múltiples {@code @Value} dispersos en {@link RateLimitConfig}.
 * Spring Boot falla rápidamente si alguna propiedad no puede enlazarse (fail-fast).</p>
 *
 * <p>Requiere {@code @EnableConfigurationProperties(RateLimitProperties.class)} o
 * la anotación {@code @ConfigurationPropertiesScan} en la clase de aplicación principal.</p>
 */
@ConfigurationProperties(prefix = "security.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        int requestsPerMinute,
        int loginRequestsPerMinute
) {
    /**
     * Constructor compacto con valores por defecto para entornos donde las
     * propiedades no están definidas explícitamente (ej. tests unitarios).
     */
    public RateLimitProperties {
        if (requestsPerMinute <= 0) {
            requestsPerMinute = 100;
        }
        if (loginRequestsPerMinute <= 0) {
            loginRequestsPerMinute = 5;
        }
    }
}
