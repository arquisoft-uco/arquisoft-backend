package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosArtefactosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class VersionRepositorioArtefactoEliminacionAutorizadaConsumer extends AbstractEventConsumer {

    private final EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor;
    private final AppLogger logger;

    public VersionRepositorioArtefactoEliminacionAutorizadaConsumer(
            EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.eliminarVersionRepositorioArtefactoInteractor = eliminarVersionRepositorioArtefactoInteractor;
        this.logger = logger;
    }

    @RabbitListener(
            queues = RepositorioArtefactosArtefactosQueueConfig.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE)
    public void onVersionRepositorioArtefactoEliminacionAutorizada(Message message, Channel channel)
            throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, VersionRepositorioArtefactoEliminacionAutorizadaPayload.class);

            logger.info(VersionRepositorioArtefactoKey.LOG_ELIMINACION_RECIBIDA,
                    payload.idEvento(), payload.versionRepositorioArtefacto());

            var resultado = eliminarVersionRepositorioArtefactoInteractor.ejecutar(
                    EliminarVersionRepositorioArtefactoCommand.crear(payload.versionRepositorioArtefacto()));

            switch (resultado) {
                case EliminacionVersionRepositorioArtefactoResult.Eliminada eliminada ->
                        logger.info(VersionRepositorioArtefactoKey.LOG_VERSION_ELIMINADA, eliminada.version());
                case EliminacionVersionRepositorioArtefactoResult.Inexistente inexistente ->
                        logger.info(VersionRepositorioArtefactoKey.LOG_VERSION_INEXISTENTE, inexistente.version());
            }
        });
    }
}
