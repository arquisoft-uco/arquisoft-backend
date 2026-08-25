package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LimiteSolicitudesProperties.class)
public class LimiteSolicitudesConfig {
}
