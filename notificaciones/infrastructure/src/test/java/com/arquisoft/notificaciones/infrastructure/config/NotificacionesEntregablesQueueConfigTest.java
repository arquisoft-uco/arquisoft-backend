package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacionesEntregablesQueueConfigTest {

    private final NotificacionesEntregablesQueueConfig config = new NotificacionesEntregablesQueueConfig();
    private final TopicExchange eventos = new TopicExchange("eventos");
    private final DirectExchange dlx = new DirectExchange("dlx");

    private void verificar(Declarables declarables, String cola, String topic) {
        assertThat(cola).isEqualTo(NotificacionesQueues.PREFIJO + topic);
        assertThat(declarables.getDeclarablesByType(Queue.class)).extracting(Queue::getName).contains(cola);
        assertThat(declarables.getDeclarablesByType(Binding.class)).anySatisfy(binding -> {
            assertThat(binding.getExchange()).isEqualTo("eventos");
            assertThat(binding.getRoutingKey()).isEqualTo(topic);
            assertThat(binding.getDestination()).isEqualTo(cola);
        });
    }

    @Test
    void debeDeclararLasCuatroColasDerivadasDelTopicConSuBinding_cuandoSeConstruyeLaConfiguracion() {
        // Act
        var generado = config.notificacionesEntregableProyectoGradoGeneradoDeclarables(eventos, dlx);
        var disponible = config.notificacionesEntregableProyectoGradoDisponibleEvaluacionDeclarables(eventos, dlx);
        var cancelado = config.notificacionesEntregableProyectoGradoCanceladoDeclarables(eventos, dlx);
        var cerrado = config.notificacionesEntregableProyectoGradoCerradoDeclarables(eventos, dlx);

        // Assert
        verificar(generado, NotificacionesEntregablesQueueConfig.ENTREGABLE_GENERADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO);
        verificar(disponible, NotificacionesEntregablesQueueConfig.ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION);
        verificar(cancelado, NotificacionesEntregablesQueueConfig.ENTREGABLE_CANCELADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO);
        verificar(cerrado, NotificacionesEntregablesQueueConfig.ENTREGABLE_CERRADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO);
    }
}
