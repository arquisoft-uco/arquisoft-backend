package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;

/**
 * Configura la externalización de {@link DomainEvent} hacia RabbitMQ mediante Spring Modulith.
 *
 * <p>Selecciona todos los eventos que extienden {@link DomainEvent} y los enruta al
 * exchange {@value RabbitMQConfig#EXCHANGE_NAME} usando {@link DomainEvent#getEventTopic()}
 * como routing key (ej. {@code "seguridad.usuario.creado"}).
 *
 * <p>{@link DomainEvent} permanece un POJO puro en {@code shared:domain} — no requiere
 * la anotación {@code @Externalized} de Spring Modulith, manteniendo la capa de dominio
 * libre de dependencias de framework.
 *
 * <p>Los headers de traza ({@code X-Trace-Id}, {@code X-User-Id}) se inyectan
 * automáticamente por el {@code MessagePostProcessor} registrado en el
 * {@link org.springframework.amqp.rabbit.core.RabbitTemplate} desde {@link RabbitMQConfig}.
 */
@Configuration
public class ModulithAmqpExternalizationConfig {

    @Bean
    EventExternalizationConfiguration eventExternalizationConfiguration() {
        return EventExternalizationConfiguration.externalizing()
                .select(event -> event instanceof DomainEvent)
                .route(DomainEvent.class, event ->
                        RoutingTarget.forTarget(RabbitMQConfig.EXCHANGE_NAME)
                                .andKey(event.getEventTopic()))
                .build();
    }
}
