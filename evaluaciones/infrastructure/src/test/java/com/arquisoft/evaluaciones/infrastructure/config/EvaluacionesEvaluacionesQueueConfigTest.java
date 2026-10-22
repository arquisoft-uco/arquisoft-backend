package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesEvaluacionesQueueConfigTest {

    @Test
    void debeDeclararColaYBinding_cuandoSeConstruyeLaConfiguracion() {
        // Arrange
        var eventos = new TopicExchange("eventos");
        var deadLetter = new DirectExchange("dlx");
        var config = new EvaluacionesEvaluacionesQueueConfig();

        // Act
        var declarables = config.evaluacionesEvaluacionesCualitativasAsesorRegistradasDeclarables(eventos, deadLetter);

        // Assert
        var colas = declarables.getDeclarablesByType(Queue.class).stream().map(Queue::getName).toList();
        assertThat(colas).contains(EvaluacionesEvaluacionesQueueConfig.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS_QUEUE);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .anySatisfy(binding -> {
                    assertThat(binding.getExchange()).isEqualTo("eventos");
                    assertThat(binding.getRoutingKey())
                            .isEqualTo(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS);
                    assertThat(binding.getDestination())
                            .isEqualTo(EvaluacionesEvaluacionesQueueConfig.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS_QUEUE);
                });
    }
}
