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

/**
 * Configuración de RabbitMQ para el exchange central de eventos de dominio.
 *
 * <p>Topología:
 * <ul>
 *   <li>{@value #EXCHANGE_NAME} — TopicExchange principal donde se publican todos los eventos.
 *   <li>{@value #DLX_NAME} — Dead Letter Exchange (direct) al que RabbitMQ reenvía mensajes
 *       que fueron rechazados (NACK sin requeue) después de agotar los reintentos del consumer.
 *       Cada contexto declara su propia DLQ y la vincula a este exchange.
 * </ul>
 *
 * <p>Publisher Confirms están habilitados vía {@code spring.rabbitmq.publisher-confirm-type=CORRELATED}
 * en {@code application.yml}. El {@link RabbitTemplate} registra callbacks de confirmación
 * y devolución para detectar mensajes no entregados.
 *
 * <p>Usa Jackson 3.x (tools.jackson) — soporte de java.time integrado en jackson-databind 3.x.
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "arquisoft.events";

    /**
     * Dead Letter Exchange: recibe los mensajes rechazados por consumers tras agotar reintentos.
     * Cada bounded context declara su propia DLQ y la vincula aquí con routing key
     * {@code {queue-name}.dead}.
     */
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

    /**
     * {@code ObjectMapper} dedicado a la capa AMQP (serialización/deserialización de mensajes).
     *
     * <p>Se nombra {@code rabbitObjectMapper} (en lugar de {@code objectMapper}) para evitar
     * colisionar con el bean auto-configurado por Spring Boot ({@code JacksonAutoConfiguration})
     * que se usa en la capa HTTP. Cada consumer lo inyecta con
     * {@code @Qualifier("rabbitObjectMapper")}.
     */
    @Bean("rabbitObjectMapper")
    public JsonMapper rabbitObjectMapper() {
        return JsonMapper.builder()
                // Tolerant Reader pattern: ignora campos desconocidos del evento.
                // Permite evolución del esquema del evento sin romper consumers existentes.
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    /**
     * Usado exclusivamente por {@link RabbitTemplate} para <b>publicar</b> eventos:
     * serializa objetos Java a JSON y añade el header {@code __TypeId__}.
     *
     * <p>Los consumers usan {@code SimpleMessageConverter} (bytes crudos) configurado
     * en {@code RabbitListenerConfig} de la aplicación principal.
     */
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter(
            @Qualifier("rabbitObjectMapper") JsonMapper rabbitObjectMapper) {
        return new JacksonJsonMessageConverter(rabbitObjectMapper);
    }

    /**
     * Inyecta headers de traza ({@code X-Trace-Id}, {@code X-User-Id}) en todos los mensajes
     * AMQP publicados, tanto desde {@link SpringModulithEventPublisher} (vía Spring Modulith)
     * como desde {@link RabbitMQEventPublisher} (fallback).
     *
     * <p>En escenarios de retry de Spring Modulith (eventos republished desde BD), el MDC puede
     * estar vacío: se generan valores de fallback (UUID aleatorio / "SYSTEM") de forma idéntica
     * a la lógica preexistente en {@link RabbitMQEventPublisher}.
     */
    @Bean
    public MessagePostProcessor traceHeadersPostProcessor() {
        return message -> {
            String traceId = MDC.get(MdcKeys.TRACE_ID);
            String userId  = MDC.get(MdcKeys.USER_ID);
            message.getMessageProperties().setHeader("X-Trace-Id",
                    traceId != null ? traceId : UUID.randomUUID().toString().replace("-", ""));
            message.getMessageProperties().setHeader("X-User-Id",
                    userId != null ? userId : "SYSTEM");
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
