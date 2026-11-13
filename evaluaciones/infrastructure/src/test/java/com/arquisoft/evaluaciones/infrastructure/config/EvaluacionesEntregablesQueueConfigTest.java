package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesEntregablesQueueConfigTest {

    private final TopicExchange eventos = new TopicExchange("eventos");
    private final DirectExchange deadLetter = new DirectExchange("dlx");
    private final EvaluacionesEntregablesQueueConfig config = new EvaluacionesEntregablesQueueConfig();

    @Test
    void debeDeclararColaYBindingDeDisponibleEvaluacion_cuandoSeConstruyeLaConfiguracion() {
        // Act
        var declarables = config.evaluacionesEntregableProyectoGradoDisponibleEvaluacionDeclarables(eventos, deadLetter);

        // Assert
        var colas = declarables.getDeclarablesByType(Queue.class).stream().map(Queue::getName).toList();
        assertThat(colas).contains(EvaluacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE);
        assertThat(EvaluacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE)
                .isEqualTo(EvaluacionesQueues.PREFIJO
                        + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .anySatisfy(binding -> {
                    assertThat(binding.getExchange()).isEqualTo("eventos");
                    assertThat(binding.getRoutingKey())
                            .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION);
                    assertThat(binding.getDestination())
                            .isEqualTo(EvaluacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE);
                });
    }
}
