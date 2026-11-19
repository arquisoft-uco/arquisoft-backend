package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesUsuariosQueueConfigTest {

    private final TopicExchange eventos = new TopicExchange("eventos");
    private final DirectExchange deadLetter = new DirectExchange("dlx");
    private final EvaluacionesUsuariosQueueConfig config = new EvaluacionesUsuariosQueueConfig();

    @Test
    void debeDeclararColasYBindingsDeAsesor_cuandoSeConstruyeLaConfiguracion() {
        // Act
        var agregado = config.evaluacionesAsesorAgregadoDeclarables(eventos, deadLetter);
        var removido = config.evaluacionesAsesorRemovidoDeclarables(eventos, deadLetter);

        // Assert
        assertThat(EvaluacionesUsuariosQueueConfig.ASESOR_AGREGADO_QUEUE)
                .isEqualTo(EvaluacionesQueues.PREFIJO + EventTopics.Usuarios.ASESOR_AGREGADO);
        assertThat(EvaluacionesUsuariosQueueConfig.ASESOR_REMOVIDO_QUEUE)
                .isEqualTo(EvaluacionesQueues.PREFIJO + EventTopics.Usuarios.ASESOR_REMOVIDO);
        assertThat(agregado.getDeclarablesByType(Queue.class).stream().map(Queue::getName).toList())
                .contains(EvaluacionesUsuariosQueueConfig.ASESOR_AGREGADO_QUEUE);
        assertThat(removido.getDeclarablesByType(Queue.class).stream().map(Queue::getName).toList())
                .contains(EvaluacionesUsuariosQueueConfig.ASESOR_REMOVIDO_QUEUE);
        assertThat(agregado.getDeclarablesByType(Binding.class)).anySatisfy(binding -> {
            assertThat(binding.getExchange()).isEqualTo("eventos");
            assertThat(binding.getRoutingKey()).isEqualTo(EventTopics.Usuarios.ASESOR_AGREGADO);
            assertThat(binding.getDestination()).isEqualTo(EvaluacionesUsuariosQueueConfig.ASESOR_AGREGADO_QUEUE);
        });
        assertThat(removido.getDeclarablesByType(Binding.class)).anySatisfy(binding -> {
            assertThat(binding.getExchange()).isEqualTo("eventos");
            assertThat(binding.getRoutingKey()).isEqualTo(EventTopics.Usuarios.ASESOR_REMOVIDO);
            assertThat(binding.getDestination()).isEqualTo(EvaluacionesUsuariosQueueConfig.ASESOR_REMOVIDO_QUEUE);
        });
    }
}
