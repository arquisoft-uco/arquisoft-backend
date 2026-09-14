package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.interactor.SincronizarProyectoEstudianteAccesoInteractor;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteProyectoAsignadoConsumer extends AbstractEventConsumer {

    private final SincronizarProyectoEstudianteAccesoInteractor sincronizarProyectoEstudianteAccesoInteractor;
    private final AppLogger logger;

    public EstudianteProyectoAsignadoConsumer(
            SincronizarProyectoEstudianteAccesoInteractor sincronizarProyectoEstudianteAccesoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.sincronizarProyectoEstudianteAccesoInteractor = sincronizarProyectoEstudianteAccesoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EvaluacionesProyectosQueueConfig.ESTUDIANTE_PROYECTO_ASIGNADO_QUEUE)
    public void onEstudianteProyectoAsignado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            EstudianteProyectoAsignadoPayload payload = deserialize(message, EstudianteProyectoAsignadoPayload.class);

            logger.info(ProyeccionAccesoEvaluacionKey.LOG_EVENTO_RECIBIDO,
                    payload.idEvento(), payload.proyectoId());

            sincronizarProyectoEstudianteAccesoInteractor.ejecutar(SincronizarProyectoEstudianteAccesoCommand.crear(
                    payload.proyectoId(),
                    payload.estudianteId(),
                    true,
                    payload.ocurridoEn()));
        });
    }
}
