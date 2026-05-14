package com.arquisoft.shared.amqp;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el {@code SimpleRabbitListenerContainerFactory} con {@link SimpleMessageConverter}
 * (bytes crudos) en lugar de {@code JacksonJsonMessageConverter}.
 *
 * <h3>Por qué es necesario</h3>
 * <p>El publisher ({@link RabbitMQEventPublisher}) serializa eventos con
 * {@code JacksonJsonMessageConverter}, que añade el header
 * {@code __TypeId__=com.arquisoft.seguridad.domain.event.UsuarioCreadoEvent}.
 * Si el listener container también usa Jackson, intenta deserializar el cuerpo del
 * mensaje en esa clase antes de invocar el método consumer — esto falla porque el
 * constructor de {@code UsuarioCreadoEvent} espera un {@code UUID usuarioId} pero el
 * JSON contiene {@code aggregateId} (String), resultando en un {@code NullPointerException}.
 *
 * <h3>Patrón del sistema</h3>
 * <p>Todos los consumers extienden {@link com.arquisoft.shared.amqp.consumer.AbstractEventConsumer}
 * y deserializan el cuerpo directamente con {@code objectMapper.readValue(message.getBody(), Payload.class)}.
 * Por tanto, el container solo necesita entregar bytes crudos — sin conversión automática.
 *
 * <h3>Separación de responsabilidades</h3>
 * <ul>
 *   <li>{@link RabbitMQConfig#rabbitTemplate} → {@code JacksonJsonMessageConverter} (publica objetos como JSON).</li>
 *   <li>Este factory → {@code SimpleMessageConverter} (entrega bytes crudos al consumer).</li>
 * </ul>
 *
 * <h3>Concurrencia</h3>
 * <p>Los valores se leen de {@code application.yml} ({@code spring.rabbitmq.listener.simple.*})
 * para que sean configurables por ambiente. El bean custom no hereda la auto-configuración
 * de Spring Boot, por lo que se inyectan explícitamente.
 */
@Configuration
public class RabbitListenerConfig {

    @Value("${spring.rabbitmq.listener.simple.concurrency:5}")
    private int concurrentConsumers;

    @Value("${spring.rabbitmq.listener.simple.max-concurrency:10}")
    private int maxConcurrentConsumers;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // Manual ACK: sincronizado con spring.rabbitmq.listener.simple.acknowledge-mode=manual
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // prefetch=1: backpressure — un consumer lento no acumula mensajes sin procesar
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        // Bytes crudos: los consumers deserializan con ObjectMapper directamente
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }
}
