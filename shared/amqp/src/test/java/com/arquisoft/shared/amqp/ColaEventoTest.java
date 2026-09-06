package com.arquisoft.shared.amqp;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ColaEventoTest {

    private static final String COLA = "notificaciones.fichas.ficha_perfil.registrada";
    private static final String ROUTING_KEY = "fichas.ficha_perfil.registrada";

    private final TopicExchange eventos = new TopicExchange("arquisoft.events");
    private final DirectExchange dlx = new DirectExchange(RabbitMQConfig.DLX_NAME);

    private Declarables declarables() {
        return ColaEvento.declarar(COLA, ROUTING_KEY, eventos, dlx);
    }

    private Queue colaLlamada(String nombre) {
        return declarables().getDeclarablesByType(Queue.class).stream()
                .filter(cola -> cola.getName().equals(nombre))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void debeDeclararLaColaYSuDescarteConSusDosBindings_cuandoSeDeclaraUnEvento() {
        // Act
        Declarables declarables = declarables();

        // Assert
        assertThat(declarables.getDeclarablesByType(Queue.class))
                .extracting(Queue::getName)
                .containsExactlyInAnyOrder(COLA, COLA + RabbitMQConfig.SUFIJO_DEAD_LETTER);
        assertThat(declarables.getDeclarablesByType(Binding.class)).hasSize(2);
    }

    @Test
    void debeApuntarLaColaAlDlx_cuandoSeDeclaraUnEvento() {
        // Act — sin estos dos argumentos el mensaje muerto se descarta sin dejar rastro
        Queue cola = colaLlamada(COLA);

        // Assert
        assertThat(cola.getArguments())
                .containsEntry(RabbitMQConfig.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConfig.DLX_NAME)
                .containsEntry(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY,
                        COLA + RabbitMQConfig.SUFIJO_DEAD_LETTER);
    }

    @Test
    void debeEnrutarLaColaDeDescarteConSuPropioNombre_cuandoSeDeclaraUnEvento() {
        // Act — el binding contra el DLX tiene que coincidir con x-dead-letter-routing-key
        Binding haciaElDescarte = declarables().getDeclarablesByType(Binding.class).stream()
                .filter(binding -> binding.getExchange().equals(RabbitMQConfig.DLX_NAME))
                .findFirst()
                .orElseThrow();

        // Assert
        assertThat(haciaElDescarte.getRoutingKey())
                .isEqualTo(COLA + RabbitMQConfig.SUFIJO_DEAD_LETTER)
                .isEqualTo(colaLlamada(COLA).getArguments()
                        .get(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY));
    }

    @Test
    void debeEnlazarLaColaAlExchangeDeEventosConElTopic_cuandoSeDeclaraUnEvento() {
        // Act
        Binding haciaLaCola = declarables().getDeclarablesByType(Binding.class).stream()
                .filter(binding -> binding.getExchange().equals(eventos.getName()))
                .findFirst()
                .orElseThrow();

        // Assert
        assertThat(haciaLaCola.getDestination()).isEqualTo(COLA);
        assertThat(haciaLaCola.getRoutingKey()).isEqualTo(ROUTING_KEY);
    }

    @Test
    void debeCaducarLosMensajesDeLaColaDeDescarte_cuandoSeDeclaraUnEvento() {
        // Act
        Queue descarte = colaLlamada(COLA + RabbitMQConfig.SUFIJO_DEAD_LETTER);

        // Assert
        assertThat(descarte.getArguments())
                .containsEntry(RabbitMQConfig.ARG_MESSAGE_TTL, RabbitMQConfig.TTL_COLA_DEAD_LETTER);
    }

    @Test
    void debeComponerElNombreConElPrefijoDelContexto_cuandoSePideElNombre() {
        // Act & Assert
        assertThat(ColaEvento.nombre("notificaciones.", ROUTING_KEY)).isEqualTo(COLA);
    }

    @Test
    void debeDeclararTodasDurables_cuandoSeDeclaraUnEvento() {
        // Act & Assert — una cola no durable pierde los eventos al reiniciar el broker
        assertThat(declarables().getDeclarablesByType(Queue.class))
                .allMatch(Queue::isDurable);
    }

    @Test
    void debeUsarLaListaDeclaradaCompleta_cuandoSeCuentanLasDeclaraciones() {
        // Act & Assert
        List<?> todas = List.copyOf(declarables().getDeclarables());
        assertThat(todas).hasSize(4);
    }
}
