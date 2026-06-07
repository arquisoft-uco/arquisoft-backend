package com.arquisoft.shared.amqp;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    @Value("${spring.rabbitmq.listener.simple.concurrency:5}")
    private int concurrentConsumers;

    @Value("${spring.rabbitmq.listener.simple.max-concurrency:10}")
    private int maxConcurrentConsumers;

    @Value("${spring.rabbitmq.listener.simple.prefetch:1}")
    private int prefetch;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // Manual ACK: sincronizado con spring.rabbitmq.listener.simple.acknowledge-mode=manual
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // prefetch: backpressure — un consumer lento no acumula mensajes sin procesar
        factory.setPrefetchCount(prefetch);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        // Bytes crudos: los consumers deserializan con ObjectMapper directamente
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }
}
