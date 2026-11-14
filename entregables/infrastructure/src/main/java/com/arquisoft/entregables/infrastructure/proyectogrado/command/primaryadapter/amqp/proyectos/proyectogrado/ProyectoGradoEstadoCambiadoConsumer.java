package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.PurgarArtefactosProyectoGradoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
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
    private final PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor;
    private final PurgarArtefactosProyectoGradoInteractor purgarArtefactosProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoEstadoCambiadoConsumer(
            ActualizarEstadoProyectoGradoInteractor actualizarEstadoProyectoGradoInteractor,
            PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor,
            PurgarArtefactosProyectoGradoInteractor purgarArtefactosProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.actualizarEstadoProyectoGradoInteractor = actualizarEstadoProyectoGradoInteractor;
        this.purgarPaquetesProyectoGradoInteractor = purgarPaquetesProyectoGradoInteractor;
        this.purgarArtefactosProyectoGradoInteractor = purgarArtefactosProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesProyectosQueueConfig.PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE)
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

            if (ESTADO_CANCELADO.equals(payload.estadoNuevo())) {
                var purgaPaquetes = purgarPaquetesProyectoGradoInteractor.ejecutar(
                        PurgarPaquetesProyectoGradoCommand.crear(payload.proyectoGradoId()));
                logger.info(PaqueteEntregableKey.LOG_PURGADOS,
                        purgaPaquetes.proyectoGrado(), purgaPaquetes.eliminados());
            }

            if (ESTADO_FINALIZADO.equals(payload.estadoNuevo()) || ESTADO_CANCELADO.equals(payload.estadoNuevo())) {
                var purgaArtefactos = purgarArtefactosProyectoGradoInteractor.ejecutar(
                        PurgarArtefactosProyectoGradoCommand.crear(payload.proyectoGradoId()));
                logger.info(ArtefactoKey.LOG_PURGADOS, purgaArtefactos.proyectoGrado(), purgaArtefactos.eliminadas());
            }
        });
    }
}
