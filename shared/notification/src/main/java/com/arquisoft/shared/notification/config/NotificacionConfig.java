package com.arquisoft.shared.notification.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Habilita el binding de {@link NotificacionProperties}.
 *
 * <p>No declara beans de proveedor: cada implementacion de {@code EnvioNotificacionOutputPort} se anota
 * como {@code @Component} con su propio {@code @ConditionalOnProperty}, de modo que agregar uno
 * nuevo no obliga a modificar esta clase.
 */
@Configuration
@EnableConfigurationProperties(NotificacionProperties.class)
public class NotificacionConfig {
}
