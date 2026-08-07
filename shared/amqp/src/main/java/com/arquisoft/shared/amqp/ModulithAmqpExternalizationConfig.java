package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;

@Configuration
public class ModulithAmqpExternalizationConfig {

    @Bean
    EventExternalizationConfiguration eventExternalizationConfiguration() {
        return EventExternalizationConfiguration.externalizing()
                .select(evento -> evento instanceof DomainEvent)
                .route(DomainEvent.class, evento ->
                        RoutingTarget.forTarget(RabbitMQConfig.EXCHANGE_NAME)
                                .andKey(evento.getTemaEvento()))
                .build();
    }
}
