package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.message.SeguridadMessages;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.util.UtilObject;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Implementacion distribuida de {@link BucketResolver} usando Bucket4j + Lettuce (Redis).
 *
 * <p>Sustituye InMemoryBucketResolver. Comportamientos preservados:</p>
 * <ul>
 *   <li>{@code rate-limit.enabled=false}  → unlimited bucket (sin cuota)</li>
 *   <li>Redis no disponible               → exhausted bucket, fail-closed (HTTP 429 inmediato)</li>
 *   <li>bucket login                      → refillGreedy (anti-ventana-fija, igual que antes)</li>
 *   <li>bucket general                    → refillIntervally (igual que antes)</li>
 * </ul>
 *
 * <p>Ventajas sobre la implementacion en memoria:</p>
 * <ul>
 *   <li>Estado compartido entre instancias (horizontal scaling)</li>
 *   <li>Sin limite de IPs rastreadas — Redis gestiona la eviction por TTL</li>
 *   <li>Sin @Scheduled de eviction manual</li>
 *   <li>Estado sobrevive reinicios de la app</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisBucketResolver implements BucketResolver, DisposableBean {

    private final RateLimitProperties properties;
    private final LettuceConnectionFactory lettuceConnectionFactory;

    private LettuceBasedProxyManager<String> proxyManager;
    private StatefulRedisConnection<String, byte[]> bucketConnection;

    @PostConstruct
    public void init() {
        // getNativeClient() retorna AbstractRedisClient directamente en Spring Data Redis 4.x
        AbstractRedisClient nativeClient = lettuceConnectionFactory.getNativeClient();
        if (nativeClient instanceof RedisClient redisClient) {
            this.bucketConnection = redisClient.connect(
                    RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            // Bucket4jLettuce.casBasedBuilder es la API no-deprecated en Bucket4j 8.x
            this.proxyManager = Bucket4jLettuce.casBasedBuilder(bucketConnection)
                    .expirationAfterWrite(ExpirationAfterWriteStrategy
                            .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(2)))
                    .build();
            log.debug(SeguridadMessages.RateLimit.INIT_OK);
        } else {
            // log.error: detalle tecnico para el desarrollador — nunca llega al cliente.
            // El nombre de la clase del cliente obtenido orienta rapidamente el diagnostico.
            log.error(SeguridadMessages.RateLimit.CLIENTE_STANDALONE_ERROR_LOG,
                    !UtilObject.isNull(nativeClient) ? nativeClient.getClass().getSimpleName() : "null");
            // InfrastructureException con mensaje generico: si llegara a la capa web
            // (improbable desde @PostConstruct), el cliente ve un mensaje sin detalles internos.
            throw new InfrastructureException(
                    SeguridadMessages.RateLimit.CLIENTE_STANDALONE_EXCEPCION_MSG,
                    SeguridadMessages.RateLimit.REDIS_CLIENTE_STANDALONE_REQUERIDO);
        }
    }

    @Override
    public Bucket resolveBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        try {
            return proxyManager.getProxy(
                    "arquisoft:ratelimit:global:" + ip,
                    () -> BucketConfiguration.builder()
                            .addLimit(limit -> limit
                                    .capacity(properties.requestsPerMinute())
                                    .refillIntervally(properties.requestsPerMinute(), Duration.ofMinutes(1)))
                            .build());
        } catch (Exception e) {
            log.error(SeguridadMessages.RateLimit.BUCKET_REDIS_ERROR,
                    ip, e.getMessage());
            return createExhaustedBucket();
        }
    }

    @Override
    public Bucket resolveLoginBucket(String ip) {
        if (!properties.enabled()) {
            return createUnlimitedBucket();
        }
        try {
            return proxyManager.getProxy(
                    "arquisoft:ratelimit:login:" + ip,
                    () -> BucketConfiguration.builder()
                            .addLimit(limit -> limit
                                    .capacity(properties.loginRequestsPerMinute())
                                    .refillGreedy(properties.loginRequestsPerMinute(), Duration.ofMinutes(1)))
                            .build());
        } catch (Exception e) {
            log.error(SeguridadMessages.RateLimit.BUCKET_LOGIN_REDIS_ERROR,
                    ip, e.getMessage());
            return createExhaustedBucket();
        }
    }

    @Override
    public boolean isRateLimitEnabled() {
        return properties.enabled();
    }

    @Override
    public void destroy() {
        if (!UtilObject.isNull(bucketConnection)) {
            bucketConnection.close();
        }
    }

    /**
     * Bucket exhausto: cualquier peticion recibe HTTP 429 de inmediato.
     * Se usa en modo fail-closed cuando Redis no esta disponible.
     * No se almacena en cache — se crea y descarta por peticion, sin consumo de memoria.
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

    /** Bucket ilimitado: usado cuando rate-limit.enabled=false. */
    private Bucket createUnlimitedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(Long.MAX_VALUE)
                        .refillIntervally(Long.MAX_VALUE, Duration.ofDays(1))
                        .build())
                .build();
    }
}
