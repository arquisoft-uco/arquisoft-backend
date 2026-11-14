package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.infrastructure.config.EntregablesProyectosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
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
public class ProyectoGradoFinalizadoConsumer extends AbstractEventConsumer {

    private final FinalizarProyectoGradoInteractor finalizarProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoFinalizadoConsumer(
            FinalizarProyectoGradoInteractor finalizarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.finalizarProyectoGradoInteractor = finalizarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = EntregablesProyectosQueueConfig.PROYECTO_GRADO_FINALIZADO_QUEUE)
    public void onProyectoGradoFinalizado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoFinalizadoPayload.class);
            logger.info(ProyectoGradoKey.LOG_FINALIZACION_RECIBIDA, payload.idEvento(), payload.proyectoGradoId());

            var resultado = finalizarProyectoGradoInteractor.ejecutar(
                    FinalizarProyectoGradoCommand.crear(payload.proyectoGradoId()));

            switch (resultado) {
                case FinalizacionProyectoGradoResult.Finalizado finalizado -> {
                    logger.info(ProyectoGradoKey.LOG_FINALIZADO, finalizado.proyectoGrado());
                    logger.info(ArtefactoKey.LOG_PURGADOS, finalizado.proyectoGrado(), finalizado.artefactosEliminados());
                }
                case FinalizacionProyectoGradoResult.Duplicado duplicado -> {
                    logger.info(ProyectoGradoKey.LOG_YA_FINALIZADO, duplicado.proyectoGrado());
                    logger.info(ArtefactoKey.LOG_PURGADOS, duplicado.proyectoGrado(), duplicado.artefactosEliminados());
                }
            }
        });
    }
}
