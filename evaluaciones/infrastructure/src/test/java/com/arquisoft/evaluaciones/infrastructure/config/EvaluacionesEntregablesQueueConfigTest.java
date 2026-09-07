package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesEntregablesQueueConfigTest {

    private final EvaluacionesEntregablesQueueConfig config = new EvaluacionesEntregablesQueueConfig();
    private final TopicExchange eventos = new TopicExchange("arquisoft.events");
    private final DirectExchange dlx = new DirectExchange(RabbitMQConfig.DLX_NAME);

    @Test
    void debeDeclararLaColaYSuDescarte_conElBindingAlTopicCorrecto() {
        // Act
        Declarables declarables = config.evaluacionesEntregableGeneradoDeclarables(eventos, dlx);

        // Assert
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .containsExactlyInAnyOrder(
                        EvaluacionesEntregablesQueueConfig.ENTREGABLE_GENERADO_QUEUE,
                        EvaluacionesEntregablesQueueConfig.ENTREGABLE_GENERADO_QUEUE + RabbitMQConfig.SUFIJO_DEAD_LETTER);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .filteredOn(binding -> binding.getExchange().equals(eventos.getName()))
                .extracting(Binding::getRoutingKey)
                .containsExactly(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO);
    }
}
