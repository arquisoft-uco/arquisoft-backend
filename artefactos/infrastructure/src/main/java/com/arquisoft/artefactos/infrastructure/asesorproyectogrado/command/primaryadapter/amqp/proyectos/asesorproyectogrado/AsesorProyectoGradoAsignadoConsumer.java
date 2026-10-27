package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.interactor.AgregarAsesorProyectoGradoInteractor;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AsesorProyectoGradoAsignadoConsumer extends AbstractEventConsumer {

    private final AgregarAsesorProyectoGradoInteractor agregarAsesorProyectoGradoInteractor;
    private final AppLogger logger;

    public AsesorProyectoGradoAsignadoConsumer(
            AgregarAsesorProyectoGradoInteractor agregarAsesorProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarAsesorProyectoGradoInteractor = agregarAsesorProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE)
    public void onAsesorProyectoGradoAsignado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorProyectoGradoAsignadoPayload.class);
            logger.info(AsesorProyectoGradoKey.LOG_ASIGNADO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.asesorId(), payload.ocurridoEn());

            var resultado = agregarAsesorProyectoGradoInteractor.ejecutar(
                    AgregarAsesorProyectoGradoCommand.crear(
                            payload.asesorId(), payload.proyectoGradoId(), payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionAsesorProyectoGradoResult.Agregada agregada ->
                        logger.info(AsesorProyectoGradoKey.LOG_AGREGADO,
                                agregada.asesor(), agregada.proyectoGrado());
                case AgregacionAsesorProyectoGradoResult.Reactivada reactivada ->
                        logger.info(AsesorProyectoGradoKey.LOG_REACTIVADO,
                                reactivada.asesor(), reactivada.proyectoGrado());
                case AgregacionAsesorProyectoGradoResult.Duplicada duplicada ->
                        logger.info(AsesorProyectoGradoKey.LOG_DUPLICADO,
                                duplicada.asesor(), duplicada.proyectoGrado());
                case AgregacionAsesorProyectoGradoResult.Descartada descartada ->
                        logger.debug(AsesorProyectoGradoKey.LOG_DESCARTADO,
                                descartada.asesor(), descartada.proyectoGrado(), descartada.ocurridoEnVigente());
            }
        });
    }
}
