package com.arquisoft.seguridad.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración de Rate Limiting usando Bucket4j.
 *
 * <p>Los buckets se almacenan en {@link ConcurrentHashMap} indexados por IP.
 * Para evitar la fuga de memoria que produce un mapa que crece sin límite,
 * se registra el último acceso de cada IP y una tarea {@code @Scheduled} elimina
 * las entradas inactivas cada 2 minutos. Como la ventana de cuota es 1 minuto,
 * un bucket no accedido en 2 minutos ya habría sido reabastecido de todos modos.</p>
 *
 * <p>El bucket de login usa {@link Refill#greedy} en lugar de {@code intervally}
 * para evitar la vulnerabilidad de ventana fija: distribuye los tokens a lo largo
 * del minuto en lugar de reponerlos todos de golpe al final del período.</p>
 *
 * <p>Las propiedades se leen de {@link RateLimitProperties} (prefijo
 * {@code security.rate-limit.*}) en lugar de múltiples {@code @Value} dispersos.</p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfig {

    private final RateLimitProperties properties;

    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBucketsByIp = new ConcurrentHashMap<>();

    /** Rastrea el último instante en que cada IP usó su bucket general. */
    private final Map<String, Instant> lastAccessByIp = new ConcurrentHashMap<>();

    /** Rastrea el último instante en que cada IP usó su bucket de login. */
    private final Map<String, Instant> lastLoginAccessByIp = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        lastAccessByIp.put(ip, Instant.now());
        return bucketsByIp.computeIfAbsent(ip, k -> createGeneralBucket());
    }

    public Bucket resolveLoginBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        lastLoginAccessByIp.put(ip, Instant.now());
        return loginBucketsByIp.computeIfAbsent(ip, k -> createLoginBucket());
    }

    /**
     * Limpia entradas inactivas cada 2 minutos para evitar fuga de memoria.
     * Una IP sin actividad durante ese período ya habría repuesto su cuota de todas formas.
     */
    @Scheduled(fixedRate = 120_000)
    public void evictStaleBuckets() {
        Instant threshold = Instant.now().minus(Duration.ofMinutes(2));

        int removedGeneral = evictFrom(bucketsByIp, lastAccessByIp, threshold);
        int removedLogin   = evictFrom(loginBucketsByIp, lastLoginAccessByIp, threshold);

        if (removedGeneral > 0 || removedLogin > 0) {
            log.debug("Rate-limit eviction: {} general + {} login buckets eliminados (total activos: {}/{})",
                    removedGeneral, removedLogin, bucketsByIp.size(), loginBucketsByIp.size());
        }
    }

    private int evictFrom(Map<String, Bucket> buckets, Map<String, Instant> lastAccess, Instant threshold) {
        int count = 0;
        for (Map.Entry<String, Instant> entry : lastAccess.entrySet()) {
            if (entry.getValue().isBefore(threshold)) {
                buckets.remove(entry.getKey());
                lastAccess.remove(entry.getKey());
                count++;
            }
        }
        return count;
    }

    /** Bucket general: ventana fija es suficiente para tráfico normal. */
    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(
                properties.requestsPerMinute(),
                Refill.intervally(properties.requestsPerMinute(), Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Bucket de login: usa {@link Refill#greedy} para distribuir los tokens gradualmente.
     * Previene el ataque de ventana fija: 3 intentos al 11:59:59 + 3 al 12:00:00 = 6 en 2 s.
     * Con greedy, los tokens se reabastecen 1 cada (60/N) segundos.
     */
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(
                properties.loginRequestsPerMinute(),
                Refill.greedy(properties.loginRequestsPerMinute(), Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createUnlimitedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(Long.MAX_VALUE, Refill.intervally(Long.MAX_VALUE, Duration.ofDays(1))))
                .build();
    }

    public boolean isRateLimitEnabled() {
        return properties.enabled();
    }
}

