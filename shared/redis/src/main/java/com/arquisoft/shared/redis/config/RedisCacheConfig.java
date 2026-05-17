package com.arquisoft.shared.redis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * CacheManager global para todos los bounded contexts.
     *
     * Prefijo de clave: "arquisoft:"
     * Convencion de uso: @Cacheable(value = "{contexto}:{nombre}", key = "#param")
     * Ejemplo key Redis resultante: arquisoft:proyectos:activos::{uuid}
     *
     * TTL por defecto: 30 minutos.
     * Para un TTL diferente, el bounded context puede declarar un
     * RedisCacheManagerBuilderCustomizer o registrar configuraciones de cache especificas.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // GenericJacksonJsonRedisSerializer: reemplazo Jackson 3.x de GenericJackson2JsonRedisSerializer.
        // Almacena tipo como @class en el JSON para deserialización correcta en @Cacheable sin conocer el tipo.
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("arquisoft:")
                .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
