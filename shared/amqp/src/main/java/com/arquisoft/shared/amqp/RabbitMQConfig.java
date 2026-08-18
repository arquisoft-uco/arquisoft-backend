package com.arquisoft.shared.amqp;

import com.arquisoft.shared.amqp.trazabilidad.TrazaMessagePostProcessor;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "arquisoft.events";

    public static final String DLX_NAME = "arquisoft.dlx";

    public static final String RABBIT_OBJECT_MAPPER = "rabbitObjectMapper";

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

    @Bean(RABBIT_OBJECT_MAPPER)
    public JsonMapper rabbitObjectMapper() {
        return JsonMapper.builder()
                // Tolerant Reader pattern: ignora campos desconocidos del evento.
                // Permite evolución del esquema del evento sin romper consumers existentes.
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter(
            @Qualifier(RABBIT_OBJECT_MAPPER) JsonMapper rabbitObjectMapper) {
        return new JacksonJsonMessageConverter(rabbitObjectMapper);
    }

    @Bean
    public MessagePostProcessor traceHeadersPostProcessor(GestorTraza gestorTraza) {
        return new TrazaMessagePostProcessor(gestorTraza);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter,
            MessagePostProcessor traceHeadersPostProcessor) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(EXCHANGE_NAME);
        // Aplica traza en todos los mensajes publicados, incluidos los enviados
        // por Spring Modulith tras el commit de transacción (Outbox retry incluido).
        template.setBeforePublishPostProcessors(traceHeadersPostProcessor);

        // Publisher Returns: si el broker no puede enrutar el mensaje a ninguna cola,
        // lo devuelve al publicador en lugar de descartarlo silenciosamente.
        template.setMandatory(true);
        template.setReturnsCallback(returned ->
            log.error(Mensajes.obtener(MensajeriaKey.LOG_MENSAJE_NO_ENRUTADO),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText())
        );

        // Publisher Confirms: el broker confirma (ACK) o rechaza (NACK) cada mensaje publicado.
        // Si el broker envía NACK, se registra el error con el correlationId del evento.
        template.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) {
                log.error(Mensajes.obtener(MensajeriaKey.LOG_BROKER_RECHAZO),
                        correlation != null ? correlation.getId()
                                : Mensajes.obtener(MensajeriaKey.VALOR_CORRELACION_DESCONOCIDA),
                        cause);
            }
        });

        return template;
    }
}
