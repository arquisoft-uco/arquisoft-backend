package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.versionrepositorioartefacto;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.AgregarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosRepositorioArtefactosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class VersionRepositorioArtefactoSubidaConsumer extends AbstractEventConsumer {

    private final AgregarVersionRepositorioArtefactoInteractor agregarVersionRepositorioArtefactoInteractor;
    private final AppLogger logger;

    public VersionRepositorioArtefactoSubidaConsumer(
            AgregarVersionRepositorioArtefactoInteractor agregarVersionRepositorioArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarVersionRepositorioArtefactoInteractor = agregarVersionRepositorioArtefactoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosRepositorioArtefactosQueueConfig.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA_QUEUE)
    public void onVersionRepositorioArtefactoSubida(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, VersionRepositorioArtefactoSubidaPayload.class);

            logger.info(AgregarVersionRepositorioArtefactoKey.LOG_VERSION_RECIBIDA,
                    payload.idEvento(), payload.versionRepositorioArtefacto(), payload.repositorioArtefacto(),
                    payload.versionDocumento());

            var resultado = agregarVersionRepositorioArtefactoInteractor.ejecutar(
                    AgregarVersionRepositorioArtefactoCommand.crear(
                            payload.versionRepositorioArtefacto(),
                            payload.versionDocumento(),
                            payload.nombreDocumento(),
                            payload.repositorioArtefacto(),
                            payload.nombreRepositorio(),
                            new AgregarVersionRepositorioArtefactoCommand.DatosArchivo(
                                    payload.nombreArchivo(), payload.tipoContenido(), payload.tamanoBytes())));

            switch (resultado) {
                case AgregacionVersionRepositorioArtefactoResult.Agregada agregada ->
                        logger.info(AgregarVersionRepositorioArtefactoKey.LOG_AGREGADA,
                                agregada.versionRepositorioArtefacto());
                case AgregacionVersionRepositorioArtefactoResult.Duplicada duplicada ->
                        logger.info(AgregarVersionRepositorioArtefactoKey.LOG_DUPLICADA,
                                duplicada.versionRepositorioArtefacto());
            }
        });
    }
}
