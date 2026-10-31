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
public class NotificacionesArtefactosQueueConfig {

    public static final String VERSION_ARTEFACTO_GENERADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA;

    public static final String ESTUDIANTES_ARTEFACTO_ASIGNADOS_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.ESTUDIANTES_ARTEFACTO_ASIGNADOS;

    public static final String ESTUDIANTE_ARTEFACTO_REMOVIDO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.ESTUDIANTE_ARTEFACTO_REMOVIDO;

    public static final String REVISION_ASESOR_ESTADO_CAMBIADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.REVISION_ASESOR_ESTADO_CAMBIADO;

    public static final String ARTEFACTO_OMITIDO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.ARTEFACTO_OMITIDO;

    public static final String REVISION_ASESOR_ENVIADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.REVISION_ASESOR_ENVIADA;

    @Bean
    public Declarables notificacionesArtefactoOmitidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ARTEFACTO_OMITIDO_QUEUE,
                EventTopics.Artefactos.ARTEFACTO_OMITIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEstudiantesArtefactoAsignadosDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTES_ARTEFACTO_ASIGNADOS_QUEUE,
                EventTopics.Artefactos.ESTUDIANTES_ARTEFACTO_ASIGNADOS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEstudianteArtefactoRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_ARTEFACTO_REMOVIDO_QUEUE,
                EventTopics.Artefactos.ESTUDIANTE_ARTEFACTO_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesVersionArtefactoGeneradaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                VERSION_ARTEFACTO_GENERADA_QUEUE,
                EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesRevisionAsesorEstadoCambiadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                REVISION_ASESOR_ESTADO_CAMBIADO_QUEUE,
                EventTopics.Artefactos.REVISION_ASESOR_ESTADO_CAMBIADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesRevisionAsesorEnviadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                REVISION_ASESOR_ENVIADA_QUEUE,
                EventTopics.Artefactos.REVISION_ASESOR_ENVIADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
