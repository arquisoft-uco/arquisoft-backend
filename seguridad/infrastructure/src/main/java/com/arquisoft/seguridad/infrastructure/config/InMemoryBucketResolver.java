package com.arquisoft.seguridad.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria de {@link BucketResolver} usando Bucket4j.
 *
 * <p>Gestiona el estado de los buckets por IP en {@link ConcurrentHashMap}.
 * Para evitar la fuga de memoria, registra el último acceso de cada IP y una tarea
 * {@code @Scheduled} elimina entradas inactivas cada 2 minutos con {@code fixedDelay}
 * (espera a que el ciclo anterior termine antes de iniciar el siguiente).</p>
 *
 * <p>El bucket de login usa {@code refillGreedy} para distribuir los tokens gradualmente
 * y prevenir el ataque de ventana fija (N intentos al 11:59:59 + N al 12:00:00).</p>
 *
 * <p><strong>Limitación de despliegue:</strong> en entornos multi-instancia (load balancer),
 * cada instancia mantiene su propio mapa. El límite efectivo es {@code N × requestsPerMinute}.
 * Para escalar horizontalmente, reemplazar este bean por una implementación
 * {@code RedisBucketResolver implements BucketResolver} usando bucket4j-redis.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryBucketResolver implements BucketResolver {

    private final RateLimitProperties properties;

    private final Map<String, Bucket> bucketsByIp = new ConcurrentHashMap<>();
    private final Map<String, Bucket> loginBucketsByIp = new ConcurrentHashMap<>();

    /** Rastrea el último instante en que cada IP usó su bucket general. */
    private final Map<String, Instant> lastAccessByIp = new ConcurrentHashMap<>();

    /** Rastrea el último instante en que cada IP usó su bucket de login. */
    private final Map<String, Instant> lastLoginAccessByIp = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        if (!bucketsByIp.containsKey(ip) && bucketsByIp.size() >= properties.maxTrackedIps()) {
            log.warn("Rate-limit: limite de IPs rastreadas alcanzado ({}/{}), rechazando IP desconocida (fail-closed)",
                    bucketsByIp.size(), properties.maxTrackedIps());
            return createExhaustedBucket();
        }
        lastAccessByIp.put(ip, Instant.now());
        return bucketsByIp.computeIfAbsent(ip, k -> createGeneralBucket());
    }

    @Override
    public Bucket resolveLoginBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        if (!loginBucketsByIp.containsKey(ip) && loginBucketsByIp.size() >= properties.maxTrackedIps()) {
            log.warn("Rate-limit: limite de IPs rastreadas (login) alcanzado ({}/{}), rechazando IP desconocida (fail-closed)",
                    loginBucketsByIp.size(), properties.maxTrackedIps());
            return createExhaustedBucket();
        }
        lastLoginAccessByIp.put(ip, Instant.now());
        return loginBucketsByIp.computeIfAbsent(ip, k -> createLoginBucket());
    }

    @Override
    public boolean isRateLimitEnabled() {
        return properties.enabled();
    }

    /**
     * Limpia entradas inactivas cada 2 minutos para evitar fuga de memoria.
     * Usa {@code fixedDelay} para garantizar que el ciclo anterior haya terminado
     * antes de iniciar el siguiente (evita solapamiento bajo carga alta).
     */
    @Scheduled(fixedDelay = 120_000)
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

    /** Bucket general: ventana fija, suficiente para tráfico normal. */
    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.requestsPerMinute())
                .refillIntervally(properties.requestsPerMinute(), Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Bucket de login: {@code refillGreedy} distribuye los tokens gradualmente.
     * Previene el ataque de ventana fija: N intentos al 11:59:59 + N al 12:00:00 = 2N en 2 s.
     * Con greedy, los tokens se reabastecen 1 cada (60/N) segundos.
     */
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.loginRequestsPerMinute())
                .refillGreedy(properties.loginRequestsPerMinute(), Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Bucket con 0 fichas disponibles: cualquier petición recibe HTTP 429 de inmediato.
     * Se usa en modo fail-closed cuando el mapa de IPs rastreadas está lleno.
     * No se almacena en el mapa — se crea y descarta por petición, sin consumo de memoria.
     */
    private Bucket createExhaustedBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(1)
                .refillIntervally(1, Duration.ofDays(1))
                .build();
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        bucket.tryConsume(1);
        return bucket;
    }

    private Bucket createUnlimitedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(Long.MAX_VALUE)
                        .refillIntervally(Long.MAX_VALUE, Duration.ofDays(1))
                        .build())
                .build();
    }
}
