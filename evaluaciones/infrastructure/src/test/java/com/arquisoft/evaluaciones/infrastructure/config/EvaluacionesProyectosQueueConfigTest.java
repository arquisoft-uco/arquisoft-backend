package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesProyectosQueueConfigTest {

    private final TopicExchange eventos = new TopicExchange("eventos");
    private final DirectExchange deadLetter = new DirectExchange("dlx");
    private final EvaluacionesProyectosQueueConfig config = new EvaluacionesProyectosQueueConfig();

    @Test
    void debeDeclararColaYBindingDeAsignados_cuandoSeConstruyeLaConfiguracion() {
        // Act
        var declarables = config.evaluacionesEstudiantesProyectoGradoAsignadosDeclarables(eventos, deadLetter);

        // Assert
        var colas = declarables.getDeclarablesByType(Queue.class).stream().map(Queue::getName).toList();
        assertThat(colas).contains(EvaluacionesProyectosQueueConfig.ESTUDIANTES_ASIGNADOS_QUEUE);
        assertThat(EvaluacionesProyectosQueueConfig.ESTUDIANTES_ASIGNADOS_QUEUE)
                .isEqualTo(EvaluacionesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .anySatisfy(binding -> {
                    assertThat(binding.getExchange()).isEqualTo("eventos");
                    assertThat(binding.getRoutingKey())
                            .isEqualTo(EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS);
                    assertThat(binding.getDestination())
                            .isEqualTo(EvaluacionesProyectosQueueConfig.ESTUDIANTES_ASIGNADOS_QUEUE);
                });
    }
}
