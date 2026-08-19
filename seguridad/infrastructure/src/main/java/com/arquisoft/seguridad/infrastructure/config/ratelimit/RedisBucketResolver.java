package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.util.UtilObjeto;
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
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisBucketResolver implements BucketResolver, DisposableBean {

    private final LimiteSolicitudesProperties properties;
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
            log.debug(Mensajes.obtener(LimiteSolicitudesKey.LOG_INIT_OK));
        } else {
            // log.error: detalle tecnico para el desarrollador — nunca llega al cliente.
            // El nombre de la clase del cliente obtenido orienta rapidamente el diagnostico.
            log.error(Mensajes.obtener(LimiteSolicitudesKey.LOG_CLIENTE_STANDALONE_ERROR),
                    !UtilObjeto.esNulo(nativeClient) ? nativeClient.getClass().getSimpleName() : "null");
            // InfrastructureException con mensaje generico: si llegara a la capa web
            // (improbable desde @PostConstruct), el cliente ve un mensaje sin detalles internos.
            throw new InfrastructureException(
                    Mensajes.obtener(LimiteSolicitudesKey.ERROR_CLIENTE_STANDALONE),
                    SeguridadCodes.LimiteSolicitudes.REDIS_CLIENTE_STANDALONE_REQUERIDO);
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
            log.error(Mensajes.obtener(LimiteSolicitudesKey.LOG_BUCKET_REDIS_ERROR),
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
            log.error(Mensajes.obtener(LimiteSolicitudesKey.LOG_BUCKET_LOGIN_REDIS_ERROR),
                    ip, e.getMessage());
            return createExhaustedBucket();
        }
    }

    @Override
    public boolean estaLimiteSolicitudesHabilitado() {
        return properties.enabled();
    }

    @Override
    public void destroy() {
        if (!UtilObjeto.esNulo(bucketConnection)) {
            bucketConnection.close();
        }
    }

    private Bucket createExhaustedBucket() {
        var limit = Bandwidth.builder()
                .capacity(1)
                .refillIntervally(1, Duration.ofDays(1))
                .build();
        var bucket = Bucket.builder().addLimit(limit).build();
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
