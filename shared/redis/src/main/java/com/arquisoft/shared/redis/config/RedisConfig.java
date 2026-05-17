package com.arquisoft.shared.redis.config;

import com.arquisoft.shared.util.UtilText;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * Crea explicitamente el LettuceConnectionFactory con credenciales opcionales.
     * Desplaza la auto-configuracion de Spring Boot (RedisAutoConfiguration) que no
     * expone username cuando se usa la propiedad spring.data.redis.username en ciertas versiones.
     * Si REDIS_USERNAME o REDIS_PASSWORD estan vacios, no se envian al servidor (compatible con Redis sin auth).
     */
    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (!UtilText.isEmptyOrNull(username)) {
            config.setUsername(username);
        }
        if (!UtilText.isEmptyOrNull(password)) {
            config.setPassword(password);
        }
        return new LettuceConnectionFactory(config);
    }

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
