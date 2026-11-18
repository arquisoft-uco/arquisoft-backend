package com.arquisoft.biblioteca.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BibliotecaUsuariosQueueConfig {

    public static final String BIBLIOTECARIO_AGREGADO_QUEUE =
            BibliotecaQueues.PREFIJO + EventTopics.Usuarios.BIBLIOTECARIO_AGREGADO;
    public static final String BIBLIOTECARIO_REMOVIDO_QUEUE =
            BibliotecaQueues.PREFIJO + EventTopics.Usuarios.BIBLIOTECARIO_REMOVIDO;
    public static final String ESTUDIANTE_AGREGADO_QUEUE =
            BibliotecaQueues.PREFIJO + EventTopics.Usuarios.ESTUDIANTE_AGREGADO;
    public static final String ESTUDIANTE_REMOVIDO_QUEUE =
            BibliotecaQueues.PREFIJO + EventTopics.Usuarios.ESTUDIANTE_REMOVIDO;

    @Bean
    public Declarables bibliotecaBibliotecarioAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                BIBLIOTECARIO_AGREGADO_QUEUE,
                EventTopics.Usuarios.BIBLIOTECARIO_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables bibliotecaBibliotecarioRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                BIBLIOTECARIO_REMOVIDO_QUEUE,
                EventTopics.Usuarios.BIBLIOTECARIO_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables bibliotecaEstudianteAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_AGREGADO_QUEUE,
                EventTopics.Usuarios.ESTUDIANTE_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables bibliotecaEstudianteRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_REMOVIDO_QUEUE,
                EventTopics.Usuarios.ESTUDIANTE_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
