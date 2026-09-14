package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacionesEvaluacionesQueueConfigTest {

    private final NotificacionesEvaluacionesQueueConfig config = new NotificacionesEvaluacionesQueueConfig();
    private final TopicExchange eventos = new TopicExchange("arquisoft.events");
    private final DirectExchange dlx = new DirectExchange(RabbitMQConfig.DLX_NAME);

    @Test
    void debeDeclararLaColaYSuDescarte_conElBindingAlTopicCorrecto() {
        // Act
        Declarables declarables = config.notificacionesEvaluacionesCualitativasJuradoRegistradasDeclarables(eventos, dlx);

        // Assert
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .containsExactlyInAnyOrder(
                        NotificacionesEvaluacionesQueueConfig.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS_QUEUE,
                        NotificacionesEvaluacionesQueueConfig.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS_QUEUE
                                + RabbitMQConfig.SUFIJO_DEAD_LETTER);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .filteredOn(binding -> binding.getExchange().equals(eventos.getName()))
                .extracting(Binding::getRoutingKey)
                .containsExactly(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS);
    }
}
