package com.arquisoft.shared.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Sobreescribe el RedisTemplate<Object,Object> por defecto de Spring Boot,
     * que usa serializacion JDK (no legible, no portable entre JVMs).
     * Usa String como clave y JSON Jackson 3.x como valor.
     *
     * StringRedisTemplate es auto-configurado por Spring Boot — no se redeclara aqui.
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // GenericJacksonJsonRedisSerializer es el reemplazo Jackson 3.x de GenericJackson2JsonRedisSerializer.
        // builder().build() crea un mapper con DefaultTyping habilitado para serialización polimórfica
        // (tipo guardado como @class en el JSON), permitiendo deserializar sin conocer el tipo en tiempo de lectura.
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
