package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.Bucket;

/**
 * Puerto de salida para la resolución de buckets de rate limiting por IP.
 *
 * <p>Define el contrato que {@link RateLimitingFilter} necesita para aplicar
 * rate limiting sin acoplarse a la implementación concreta {@link RateLimitConfig}.
 * Esto permite reemplazar la estrategia de almacenamiento (en memoria, Redis, etc.)
 * sin modificar el filtro (DIP — Dependency Inversion Principle).</p>
 */
public interface BucketResolver {

    /**
     * Resuelve el bucket de cuota general para la IP dada.
     *
     * @param ip dirección IP del cliente
     * @return bucket a consumir; nunca {@code null}
     */
    Bucket resolveBucket(String ip);

    /**
     * Resuelve el bucket de cuota de login para la IP dada.
     * Usa una política más restrictiva que el bucket general.
     *
     * @param ip dirección IP del cliente
     * @return bucket a consumir; nunca {@code null}
     */
    Bucket resolveLoginBucket(String ip);

    /**
     * Indica si el rate limiting está activo en el entorno actual.
     *
     * @return {@code true} si el rate limiting está habilitado
     */
    boolean isRateLimitEnabled();
}
