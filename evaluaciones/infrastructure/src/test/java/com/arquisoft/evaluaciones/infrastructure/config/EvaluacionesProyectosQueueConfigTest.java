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

class EvaluacionesProyectosQueueConfigTest {

    private final EvaluacionesProyectosQueueConfig config = new EvaluacionesProyectosQueueConfig();
    private final TopicExchange eventos = new TopicExchange("arquisoft.events");
    private final DirectExchange dlx = new DirectExchange(RabbitMQConfig.DLX_NAME);

    @Test
    void debeDeclararLaColaDeAsignacion_conSuBindingAlTopicCorrecto() {
        // Act
        Declarables declarables = config.evaluacionesEstudianteProyectoAsignadoDeclarables(eventos, dlx);

        // Assert
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .contains(EvaluacionesProyectosQueueConfig.ESTUDIANTE_PROYECTO_ASIGNADO_QUEUE);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .filteredOn(binding -> binding.getExchange().equals(eventos.getName()))
                .extracting(Binding::getRoutingKey)
                .containsExactly(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_ASIGNADO);
    }

    @Test
    void debeDeclararLaColaDeDestitucion_conSuBindingAlTopicCorrecto() {
        // Act
        Declarables declarables = config.evaluacionesEstudianteProyectoDestituidoDeclarables(eventos, dlx);

        // Assert
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .contains(EvaluacionesProyectosQueueConfig.ESTUDIANTE_PROYECTO_DESTITUIDO_QUEUE);
        assertThat(declarables.getDeclarablesByType(Binding.class))
                .filteredOn(binding -> binding.getExchange().equals(eventos.getName()))
                .extracting(Binding::getRoutingKey)
                .containsExactly(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_DESTITUIDO);
    }
}
