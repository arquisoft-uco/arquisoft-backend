package com.arquisoft.solicitudes.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolicitudesUsuariosQueueConfig {

    // El nombre de la cola tiene que seguir siendo una expresion constante: @RabbitListener lo lee
    // como valor de anotacion (JLS 9.7.1), asi que no puede salir de una llamada a metodo.
    public static final String COORDINADOR_AGREGADO_QUEUE =
            SolicitudesQueues.PREFIJO + EventTopics.Usuarios.COORDINADOR_AGREGADO;

    @Bean
    public Declarables solicitudesCoordinadorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                COORDINADOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.COORDINADOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ESTUDIANTE_AGREGADO_QUEUE =
            SolicitudesQueues.PREFIJO + EventTopics.Usuarios.ESTUDIANTE_AGREGADO;

    @Bean
    public Declarables solicitudesEstudianteAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_AGREGADO_QUEUE,
                EventTopics.Usuarios.ESTUDIANTE_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_AGREGADO_QUEUE =
            SolicitudesQueues.PREFIJO + EventTopics.Usuarios.ASESOR_AGREGADO;

    @Bean
    public Declarables solicitudesAsesorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.ASESOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String USUARIO_MODIFICADO_QUEUE =
            SolicitudesQueues.PREFIJO + EventTopics.Usuarios.USUARIO_MODIFICADO;

    @Bean
    public Declarables solicitudesUsuarioModificadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                USUARIO_MODIFICADO_QUEUE,
                EventTopics.Usuarios.USUARIO_MODIFICADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
