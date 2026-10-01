package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificacionesFichasQueueConfig {

    // El nombre de la cola tiene que seguir siendo una expresion constante: @RabbitListener lo lee
    // como valor de anotacion (JLS 9.7.1), asi que no puede salir de una llamada a metodo.
    public static final String ASESOR_CAMBIADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;

    public static final String FICHA_REGISTRADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_REGISTRADA;

    public static final String ESTUDIANTES_ASIGNADOS_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Fichas.ESTUDIANTES_FICHA_PERFIL_ASIGNADOS;

    public static final String REVISION_ITEM_AGREGADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Fichas.REVISION_ITEM_AGREGADO;

    @Bean
    public Declarables notificacionesAsesorCambiadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_CAMBIADO_QUEUE,
                EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesFichaRegistradaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                FICHA_REGISTRADA_QUEUE,
                EventTopics.Fichas.FICHA_PERFIL_REGISTRADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEstudiantesAsignadosDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTES_ASIGNADOS_QUEUE,
                EventTopics.Fichas.ESTUDIANTES_FICHA_PERFIL_ASIGNADOS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesRevisionItemAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                REVISION_ITEM_AGREGADO_QUEUE,
                EventTopics.Fichas.REVISION_ITEM_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
