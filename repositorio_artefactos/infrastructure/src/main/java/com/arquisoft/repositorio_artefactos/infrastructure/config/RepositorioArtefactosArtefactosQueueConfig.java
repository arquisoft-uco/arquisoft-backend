package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RepositorioArtefactosArtefactosQueueConfig {

    public static final String VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE =
            RepositorioArtefactosQueues.PREFIJO
                    + EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA;

    public static final String VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_1M_QUEUE =
            VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE + ".espera.1m";

    public static final String VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_5M_QUEUE =
            VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE + ".espera.5m";

    public static final List<String> VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERAS = List.of(
            VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_1M_QUEUE,
            VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_5M_QUEUE);

    private static final int UN_MINUTO_MS = 60 * 1000;
    private static final int CINCO_MINUTOS_MS = 5 * 60 * 1000;

    @Bean
    public Declarables repositorioArtefactosVersionRepositorioArtefactoEliminacionAutorizadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE,
                EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    // Colas sin consumidor: el mensaje espera su TTL y el broker lo devuelve a la cola principal por
    // el exchange por defecto, que enruta por nombre de cola. Volver por el exchange de eventos lo
    // entregaria tambien a cualquier otro contexto suscrito al mismo topic.
    @Bean
    public Declarables repositorioArtefactosVersionRepositorioArtefactoEliminacionAutorizadaEsperaDeclarables() {
        return new Declarables(
                colaEspera(VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_1M_QUEUE, UN_MINUTO_MS),
                colaEspera(VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_5M_QUEUE, CINCO_MINUTOS_MS));
    }

    private static Queue colaEspera(String nombre, int ttlMs) {
        return QueueBuilder
                .durable(nombre)
                .withArgument(RabbitMQConfig.ARG_MESSAGE_TTL, ttlMs)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConfig.EXCHANGE_POR_DEFECTO)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY,
                        VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE)
                .build();
    }
}
