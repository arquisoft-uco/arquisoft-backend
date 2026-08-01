package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        @Min(value = 1, message = "requests-per-minute debe ser >= 1") int requestsPerMinute,
        @Min(value = 1, message = "login-requests-per-minute debe ser >= 1") int loginRequestsPerMinute
) {
}
