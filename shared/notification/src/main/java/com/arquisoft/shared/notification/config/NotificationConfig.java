package com.arquisoft.shared.notification.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Habilita el binding de {@link NotificationProperties}.
 *
 * <p>No declara beans de proveedor: cada implementacion de {@code NotificationSender} se anota
 * como {@code @Component} con su propio {@code @ConditionalOnProperty}, de modo que agregar uno
 * nuevo no obliga a modificar esta clase.
 */
@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfig {
}
