package com.arquisoft.shared.redis;

/**
 * Cliente para interactuar con Redis.
 * Maneja cache, sesiones y almacenamiento temporal.
 */
public interface RedisClient {
    /**
     * Obtiene un valor del cache.
     */
    <T> T get(String key, Class<T> type);

    /**
     * Almacena un valor en el cache.
     */
    void set(String key, Object value, long ttlSeconds);

    /**
     * Elimina una clave del cache.
     */
    void delete(String key);

    /**
     * Verifica si existe una clave.
     */
    boolean exists(String key);

    /**
     * Incrementa un contador.
     */
    long increment(String key);
}
