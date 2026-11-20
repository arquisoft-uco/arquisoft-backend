package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacionesEvaluacionesQueueConfigTest {

    @Test
    void debeDeclararColaDerivadaDelTopicYBinding_cuandoSeConstruyeLaConfiguracion() {
        // Arrange
        var config = new NotificacionesEvaluacionesQueueConfig();

        // Act
        var declarables = config.notificacionesEvaluacionFinalizadaDeclarables(
                new TopicExchange("eventos"), new DirectExchange("dlx"));

        // Assert
        assertThat(NotificacionesEvaluacionesQueueConfig.EVALUACION_FINALIZADA_QUEUE)
                .isEqualTo(NotificacionesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACION_FINALIZADA);
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .contains(NotificacionesEvaluacionesQueueConfig.EVALUACION_FINALIZADA_QUEUE);
        assertThat(declarables.getDeclarablesByType(Binding.class)).anySatisfy(binding -> {
            assertThat(binding.getExchange()).isEqualTo("eventos");
            assertThat(binding.getRoutingKey()).isEqualTo(EventTopics.Evaluaciones.EVALUACION_FINALIZADA);
            assertThat(binding.getDestination())
                    .isEqualTo(NotificacionesEvaluacionesQueueConfig.EVALUACION_FINALIZADA_QUEUE);
        });
    }
}
