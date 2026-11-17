package com.arquisoft.entregables.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntregablesProyectosQueueConfig {

    public static final String PROYECTO_GRADO_REGISTRADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO;
    public static final String ESTUDIANTES_ASIGNADOS_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS;
    public static final String ESTUDIANTE_DESTITUIDO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO;
    public static final String PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO;
    public static final String ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO;
    public static final String ASESOR_PROYECTO_GRADO_FINALIZADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO;

    @Bean
    public Declarables entregablesProyectoGradoRegistradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PROYECTO_GRADO_REGISTRADO_QUEUE,
                EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesEstudiantesProyectoGradoAsignadosDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTES_ASIGNADOS_QUEUE,
                EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesEstudianteProyectoGradoDestituidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_DESTITUIDO_QUEUE,
                EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesProyectoGradoEstadoCambiadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE,
                EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesAsesorProyectoGradoAsignadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE,
                EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesAsesorProyectoGradoFinalizadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_PROYECTO_GRADO_FINALIZADO_QUEUE,
                EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
