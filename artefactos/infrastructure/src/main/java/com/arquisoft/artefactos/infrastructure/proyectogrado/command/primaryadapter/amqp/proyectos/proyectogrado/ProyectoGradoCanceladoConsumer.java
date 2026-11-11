package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
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
public class ProyectoGradoCanceladoConsumer extends AbstractEventConsumer {

    private static final String UN_SOLO_CONSUMIDOR = "1";

    private final CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor;
    private final PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    private final PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoCanceladoConsumer(
            CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor,
            PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor,
            PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.cancelarProyectoGradoInteractor = cancelarProyectoGradoInteractor;
        this.purgarVersionesProyectoGradoInteractor = purgarVersionesProyectoGradoInteractor;
        this.purgarPaquetesProyectoGradoInteractor = purgarPaquetesProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.PROYECTO_GRADO_CANCELADO_QUEUE,
            concurrency = UN_SOLO_CONSUMIDOR)
    public void onProyectoGradoCancelado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoCanceladoPayload.class);
            logger.info(ProyectoGradoKey.LOG_CANCELACION_PROYECTO_RECIBIDA,
                    payload.idEvento(), payload.proyectoGradoId(), payload.ocurridoEn());

            var cancelacion = cancelarProyectoGradoInteractor.ejecutar(
                    CancelarProyectoGradoCommand.crear(payload.proyectoGradoId(), payload.ocurridoEn()));

            switch (cancelacion) {
                case CancelacionProyectoGradoResult.Cancelado cancelado ->
                        logger.info(ProyectoGradoKey.LOG_CANCELADO, cancelado.proyectoGrado());
                case CancelacionProyectoGradoResult.YaCancelado yaCancelado ->
                        logger.info(ProyectoGradoKey.LOG_YA_CANCELADO, yaCancelado.proyectoGrado());
            }

            var purga = purgarVersionesProyectoGradoInteractor.ejecutar(
                    PurgarVersionesProyectoGradoCommand.crear(payload.proyectoGradoId()));
            logger.info(VersionArtefactoKey.LOG_PURGADAS, purga.proyectoGrado(), purga.archivos());

            var purgaPaquetes = purgarPaquetesProyectoGradoInteractor.ejecutar(
                    PurgarPaquetesProyectoGradoCommand.crear(payload.proyectoGradoId()));
            logger.info(PaqueteEntregableKey.LOG_PROYECTO_PURGADOS,
                    purgaPaquetes.proyectoGrado(), purgaPaquetes.eliminados());
        });
    }
}
