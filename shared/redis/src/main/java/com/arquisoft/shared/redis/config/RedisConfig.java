package com.arquisoft.shared.redis.config;

import com.arquisoft.shared.util.UtilTexto;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

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

    @Value("${spring.data.redis.timeout}")
    private Duration timeout;

    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(host, port);
        if (!UtilTexto.esVacioONulo(username)) {
            config.setUsername(username);
        }
        if (!UtilTexto.esVacioONulo(password)) {
            config.setPassword(password);
        }
        return new LettuceConnectionFactory(config, clientConfiguration());
    }

    // La fábrica se construye a mano, así que Spring Boot no aplica aquí spring.data.redis.timeout
    // ni las opciones de cliente. Por defecto Lettuce encola indefinidamente los comandos con el
    // canal caído, y el timeout no cuenta hasta que el comando se escribe en él: la combinación
    // cuelga la petición para siempre.
    private LettuceClientConfiguration clientConfiguration() {
        var opciones = ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .timeoutOptions(TimeoutOptions.enabled())
                .build();

        return LettuceClientConfiguration.builder()
                .clientOptions(opciones)
                .commandTimeout(timeout)
                .build();
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        var stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // GenericJacksonJsonRedisSerializer es el reemplazo Jackson 3.x de GenericJackson2JsonRedisSerializer.
        // builder().build() crea un mapper con DefaultTyping habilitado para serialización polimórfica
        // (tipo guardado como @class en el JSON), permitiendo deserializar sin conocer el tipo en tiempo de lectura.
        var jsonSerializer = GenericJacksonJsonRedisSerializer.builder().build();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
