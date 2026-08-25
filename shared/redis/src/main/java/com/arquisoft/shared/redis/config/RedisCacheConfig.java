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

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // GenericJacksonJsonRedisSerializer: reemplazo Jackson 3.x de GenericJackson2JsonRedisSerializer.
        // Almacena tipo como @class en el JSON para deserialización correcta en @Cacheable sin conocer el tipo.
        var jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("arquisoft:")
                .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
