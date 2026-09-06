package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LimiteSolicitudesProperties.class)
public class LimiteSolicitudesConfig {

    // Se declara aqui, y no dentro del resolver, porque su tope sale de las propiedades: como bean
    // el resolver lo recibe por constructor y queda construible en un test sin levantar Redis.
    @Bean
    public BucketsLocales bucketsLocales(LimiteSolicitudesProperties properties) {
        return new BucketsLocales(properties.maxTrackedIps());
    }
}
