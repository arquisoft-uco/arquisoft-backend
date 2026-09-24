package com.arquisoft.proyectos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProyectosUsuariosQueueConfig {

    public static final String COORDINADOR_AGREGADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.COORDINADOR_AGREGADO;

    @Bean
    public Declarables proyectosCoordinadorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                COORDINADOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.COORDINADOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_AGREGADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.ASESOR_AGREGADO;

    @Bean
    public Declarables proyectosAsesorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.ASESOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_REMOVIDO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.ASESOR_REMOVIDO;

    @Bean
    public Declarables proyectosAsesorRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_REMOVIDO_QUEUE,
                EventTopics.Usuarios.ASESOR_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ESTUDIANTE_AGREGADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.ESTUDIANTE_AGREGADO;

    @Bean
    public Declarables proyectosEstudianteAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_AGREGADO_QUEUE,
                EventTopics.Usuarios.ESTUDIANTE_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ESTUDIANTE_REMOVIDO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.ESTUDIANTE_REMOVIDO;

    @Bean
    public Declarables proyectosEstudianteRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_REMOVIDO_QUEUE,
                EventTopics.Usuarios.ESTUDIANTE_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String USUARIO_MODIFICADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.USUARIO_MODIFICADO;

    @Bean
    public Declarables proyectosUsuarioModificadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                USUARIO_MODIFICADO_QUEUE,
                EventTopics.Usuarios.USUARIO_MODIFICADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
