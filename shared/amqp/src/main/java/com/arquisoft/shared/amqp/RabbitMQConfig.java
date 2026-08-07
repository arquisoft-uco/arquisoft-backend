package com.arquisoft.shared.amqp;

import com.arquisoft.shared.logger.MdcKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "arquisoft.events";

    public static final String DLX_NAME = "arquisoft.dlx";

    @Bean
    public TopicExchange arquisoftEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange arquisoftDeadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX_NAME)
                .durable(true)
                .build();
    }

    @Bean("rabbitObjectMapper")
    public JsonMapper rabbitObjectMapper() {
        return JsonMapper.builder()
                // Tolerant Reader pattern: ignora campos desconocidos del evento.
                // Permite evolución del esquema del evento sin romper consumers existentes.
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter(
            @Qualifier("rabbitObjectMapper") JsonMapper rabbitObjectMapper) {
        return new JacksonJsonMessageConverter(rabbitObjectMapper);
    }

    @Bean
    public MessagePostProcessor traceHeadersPostProcessor() {
        return message -> {
            String idTraza = MDC.get(MdcKeys.ID_TRAZA);
            String idUsuario = MDC.get(MdcKeys.ID_USUARIO);
            message.getMessageProperties().setHeader("X-Trace-Id",
                    idTraza != null ? idTraza : UUID.randomUUID().toString().replace("-", ""));
            message.getMessageProperties().setHeader("X-User-Id",
                    idUsuario != null ? idUsuario : "SYSTEM");
            return message;
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter,
            MessagePostProcessor traceHeadersPostProcessor) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(EXCHANGE_NAME);
        // Aplica traza en todos los mensajes publicados, incluidos los enviados
        // por Spring Modulith tras el commit de transacción (Outbox retry incluido).
        template.setBeforePublishPostProcessors(traceHeadersPostProcessor);

        // Publisher Returns: si el broker no puede enrutar el mensaje a ninguna cola,
        // lo devuelve al publicador en lugar de descartarlo silenciosamente.
        template.setMandatory(true);
        template.setReturnsCallback(returned ->
            log.error("Mensaje no enrutado — exchange={} routingKey={} replyText={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText())
        );

        // Publisher Confirms: el broker confirma (ACK) o rechaza (NACK) cada mensaje publicado.
        // Si el broker envía NACK, se registra el error con el correlationId del evento.
        template.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) {
                log.error("Broker rechazó el mensaje (NACK) — correlationId={} causa={}",
                        correlation != null ? correlation.getId() : "desconocido", cause);
            }
        });

        return template;
    }
}
