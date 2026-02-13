package com.arquisoft.shared.security.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración de Rate Limiting usando Bucket4j.
 * Limita el número de solicitudes por IP en un período de tiempo.
 * 
 * Las propiedades se configuran en application.properties:
 * - security.rate-limit.enabled
 * - security.rate-limit.requests-per-minute
 * - security.rate-limit.login-requests-per-minute
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {
    
    @Value("${security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${security.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;
    
    @Value("${security.rate-limit.login-requests-per-minute:5}")
    private int loginRequestsPerMinute;
    
    // Cache de buckets por IP
    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBucketsByIp = new ConcurrentHashMap<>();

    /**
     * Obtiene o crea un bucket para una IP específica.
     * El bucket permite un número limitado de solicitudes por minuto.
     */
    public Bucket resolveBucket(String ip) {
        if (!rateLimitEnabled) {
            return createUnlimitedBucket();
        }
        
        return bucketsByIp.computeIfAbsent(ip, k -> createBucket(requestsPerMinute));
    }

    /**
     * Obtiene o crea un bucket para solicitudes de login.
     * El bucket para login es más restrictivo.
     */
    public Bucket resolveLoginBucket(String ip) {
        if (!rateLimitEnabled) {
            return createUnlimitedBucket();
        }
        
        return loginBucketsByIp.computeIfAbsent(ip, k -> createBucket(loginRequestsPerMinute));
    }

    /**
     * Crea un bucket con la capacidad especificada (solicitudes por minuto).
     */
    private Bucket createBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(
                requestsPerMinute,
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))
        );
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Crea un bucket sin límites (para cuando rate limiting está deshabilitado).
     */
    private Bucket createUnlimitedBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(Integer.MAX_VALUE, Refill.intervally(Integer.MAX_VALUE, Duration.ofMinutes(1))))
                .build();
    }

    public boolean isRateLimitEnabled() {
        return rateLimitEnabled;
    }
}
