package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosProyectosQueueConfig;
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
public class ProyectoGradoFinalizadoConsumer extends AbstractEventConsumer {

    private static final String UN_SOLO_CONSUMIDOR = "1";

    private final FinalizarProyectoGradoInteractor finalizarProyectoGradoInteractor;
    private final PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    private final AppLogger logger;

    public ProyectoGradoFinalizadoConsumer(
            FinalizarProyectoGradoInteractor finalizarProyectoGradoInteractor,
            PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.finalizarProyectoGradoInteractor = finalizarProyectoGradoInteractor;
        this.purgarVersionesProyectoGradoInteractor = purgarVersionesProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosProyectosQueueConfig.PROYECTO_GRADO_FINALIZADO_QUEUE,
            concurrency = UN_SOLO_CONSUMIDOR)
    public void onProyectoGradoFinalizado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, ProyectoGradoFinalizadoPayload.class);
            logger.info(ProyectoGradoKey.LOG_FINALIZACION_RECIBIDA,
                    payload.idEvento(), payload.proyectoGradoId(), payload.ocurridoEn());

            var finalizacion = finalizarProyectoGradoInteractor.ejecutar(
                    FinalizarProyectoGradoCommand.crear(payload.proyectoGradoId(), payload.ocurridoEn()));

            switch (finalizacion) {
                case FinalizacionProyectoGradoResult.Finalizado finalizado ->
                        logger.info(ProyectoGradoKey.LOG_FINALIZADO, finalizado.proyectoGrado());
                case FinalizacionProyectoGradoResult.YaFinalizado yaFinalizado ->
                        logger.info(ProyectoGradoKey.LOG_YA_FINALIZADO, yaFinalizado.proyectoGrado());
            }

            var purga = purgarVersionesProyectoGradoInteractor.ejecutar(
                    PurgarVersionesProyectoGradoCommand.crear(payload.proyectoGradoId()));
            logger.info(VersionArtefactoKey.LOG_PURGADAS, purga.proyectoGrado(), purga.archivos());
        });
    }
}
