package com.arquisoft.seguridad.infrastructure.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de configuración para el rate limiting.
 *
 * <p>Centraliza el prefijo {@code security.rate-limit.*} en un record validado
 * en startup. {@code @Validated} activa JSR-303 sobre los campos del record:
 * si algún valor de entorno es inválido (<=0), Spring Boot falla al arrancar
 * con un mensaje claro en lugar de continuar con un default silencioso.</p>
 *
 * <p>Valores de referencia para configurar en variables de entorno:
 * <ul>
 *   <li>{@code RATE_LIMIT_REQUESTS_PER_MINUTE} — mínimo 1, recomendado 60-100</li>
 *   <li>{@code RATE_LIMIT_LOGIN_REQUESTS_PER_MINUTE} — mínimo 1, recomendado 3-5</li>
 * </ul></p>
 */
@Validated
@ConfigurationProperties(prefix = "security.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        @Min(value = 1, message = "requests-per-minute debe ser >= 1") int requestsPerMinute,
        @Min(value = 1, message = "login-requests-per-minute debe ser >= 1") int loginRequestsPerMinute,
        @Min(value = 1, message = "max-tracked-ips debe ser >= 1") int maxTrackedIps
) {
}
