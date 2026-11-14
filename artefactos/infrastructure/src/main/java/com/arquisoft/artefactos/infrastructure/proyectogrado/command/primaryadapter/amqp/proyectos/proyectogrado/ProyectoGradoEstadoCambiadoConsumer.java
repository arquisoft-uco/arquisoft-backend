package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.RabbitListenerConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class ProyectoGradoEstadoCambiadoConsumer extends AbstractEventConsumer {

    private static final String ESTADO_FINALIZADO = "FINALIZADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";

    private final ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor;
    private final PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoEstadoCambiadoConsumer(
            ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor,
            PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.actualizarEstadoProyectoGradoInteractor = actualizarEstadoProyectoGradoInteractor;
        this.purgarVersionesProyectoGradoInteractor = purgarVersionesProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE,
            concurrency = RabbitListenerConfig.UN_SOLO_CONSUMIDOR)
    public void onProyectoGradoEstadoCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoEstadoCambiadoPayload.class);
            logger.info(ProyectoGradoKey.LOG_ESTADO_RECIBIDO,
                    payload.idEvento(), payload.proyectoGradoId(), payload.estadoNuevo(), payload.ocurridoEn());

            var actualizacion = actualizarEstadoProyectoGradoInteractor.ejecutar(
                    ActualizarEstadoProyectoGradoCommand.crear(
                            payload.proyectoGradoId(), payload.estadoNuevo(), payload.ocurridoEn()));

            switch (actualizacion) {
                case ActualizacionEstadoProyectoGradoResult.Actualizado actualizado ->
                        logger.info(ProyectoGradoKey.LOG_ESTADO_APLICADO,
                                actualizado.proyectoGrado(), actualizado.estadoNuevo());
                case ActualizacionEstadoProyectoGradoResult.Descartado descartado ->
                        logger.info(ProyectoGradoKey.LOG_ESTADO_DESCARTADO,
                                descartado.proyectoGrado(), descartado.ocurridoEnGuardado());
            }

            if (ESTADO_FINALIZADO.equals(payload.estadoNuevo()) || ESTADO_CANCELADO.equals(payload.estadoNuevo())) {
                var purga = purgarVersionesProyectoGradoInteractor.ejecutar(
                        PurgarVersionesProyectoGradoCommand.crear(payload.proyectoGradoId()));
                logger.info(VersionArtefactoKey.LOG_PURGADAS, purga.proyectoGrado(), purga.archivos());
            }
        });
    }
}
