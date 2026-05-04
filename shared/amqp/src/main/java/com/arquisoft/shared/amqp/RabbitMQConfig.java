package com.arquisoft.shared.amqp;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Configuración de RabbitMQ para el exchange central de eventos de dominio.
 * Usa Jackson 3.x (tools.jackson) — soporte de java.time integrado en jackson-databind 3.x.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "arquisoft.events";

    @Bean
    public TopicExchange arquisoftEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter(JsonMapper.builder().build());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(EXCHANGE_NAME);
        return template;
    }
}
