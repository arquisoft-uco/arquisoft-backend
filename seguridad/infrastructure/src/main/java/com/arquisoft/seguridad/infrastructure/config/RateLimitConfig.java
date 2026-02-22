package com.arquisoft.seguridad.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
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
    
    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBucketsByIp = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        if (!rateLimitEnabled) {
            return createUnlimitedBucket();
        }
        return bucketsByIp.computeIfAbsent(ip, k -> createBucket(requestsPerMinute));
    }

    public Bucket resolveLoginBucket(String ip) {
        if (!rateLimitEnabled) {
            return createUnlimitedBucket();
        }
        return loginBucketsByIp.computeIfAbsent(ip, k -> createBucket(loginRequestsPerMinute));
    }

    private Bucket createBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(
                requestsPerMinute,
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createUnlimitedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(Integer.MAX_VALUE, Refill.intervally(Integer.MAX_VALUE, Duration.ofMinutes(1))))
                .build();
    }

    public boolean isRateLimitEnabled() {
        return rateLimitEnabled;
    }
}
