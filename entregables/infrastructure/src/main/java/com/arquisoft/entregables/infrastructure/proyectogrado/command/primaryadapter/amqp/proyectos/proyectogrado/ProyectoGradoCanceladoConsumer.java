package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.PurgarArtefactosProyectoGradoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
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
public class ProyectoGradoCanceladoConsumer extends AbstractEventConsumer {

    private final CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor;
    private final PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor;
    private final PurgarArtefactosProyectoGradoInteractor purgarArtefactosProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoCanceladoConsumer(
            CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor,
            PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor,
            PurgarArtefactosProyectoGradoInteractor purgarArtefactosProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.cancelarProyectoGradoInteractor = cancelarProyectoGradoInteractor;
        this.purgarPaquetesProyectoGradoInteractor = purgarPaquetesProyectoGradoInteractor;
        this.purgarArtefactosProyectoGradoInteractor = purgarArtefactosProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesProyectosQueueConfig.PROYECTO_GRADO_CANCELADO_QUEUE)
    public void onProyectoGradoCancelado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoCanceladoPayload.class);
            logger.info(ProyectoGradoKey.LOG_CANCELACION_RECIBIDA, payload.idEvento(), payload.proyectoGradoId());

            var resultado = cancelarProyectoGradoInteractor.ejecutar(
                    CancelarProyectoGradoCommand.crear(payload.proyectoGradoId()));

            switch (resultado) {
                case CancelacionProyectoGradoResult.Cancelado cancelado ->
                        logger.info(ProyectoGradoKey.LOG_CANCELADO, cancelado.proyectoGrado());
                case CancelacionProyectoGradoResult.Duplicado duplicado ->
                        logger.info(ProyectoGradoKey.LOG_YA_CANCELADO, duplicado.proyectoGrado());
            }

            var purgaPaquetes = purgarPaquetesProyectoGradoInteractor.ejecutar(
                    PurgarPaquetesProyectoGradoCommand.crear(payload.proyectoGradoId()));
            logger.info(PaqueteEntregableKey.LOG_PURGADOS, purgaPaquetes.proyectoGrado(), purgaPaquetes.eliminados());

            var purgaArtefactos = purgarArtefactosProyectoGradoInteractor.ejecutar(
                    PurgarArtefactosProyectoGradoCommand.crear(payload.proyectoGradoId()));
            logger.info(ArtefactoKey.LOG_PURGADOS, purgaArtefactos.proyectoGrado(), purgaArtefactos.eliminadas());
        });
    }
}
