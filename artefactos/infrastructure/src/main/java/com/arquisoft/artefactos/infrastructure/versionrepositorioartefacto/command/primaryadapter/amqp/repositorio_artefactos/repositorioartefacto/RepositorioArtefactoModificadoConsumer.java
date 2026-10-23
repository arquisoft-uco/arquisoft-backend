package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.repositorioartefacto;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.CambiarNombreRepositorioVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.CambiarNombreRepositorioVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosRepositorioArtefactosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarNombreRepositorioVersionRepositorioArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RepositorioArtefactoModificadoConsumer extends AbstractEventConsumer {

    private final CambiarNombreRepositorioVersionRepositorioArtefactoInteractor cambiarNombreRepositorioInteractor;
    private final AppLogger logger;

    public RepositorioArtefactoModificadoConsumer(
            CambiarNombreRepositorioVersionRepositorioArtefactoInteractor cambiarNombreRepositorioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.cambiarNombreRepositorioInteractor = cambiarNombreRepositorioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosRepositorioArtefactosQueueConfig.REPOSITORIO_ARTEFACTO_MODIFICADO_QUEUE)
    public void onRepositorioArtefactoModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, RepositorioArtefactoModificadoPayload.class);

            logger.info(CambiarNombreRepositorioVersionRepositorioArtefactoKey.LOG_REPOSITORIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.repositorioArtefacto());

            cambiarNombreRepositorioInteractor.ejecutar(
                    CambiarNombreRepositorioVersionRepositorioArtefactoCommand.crear(
                            payload.repositorioArtefacto(), payload.nombreRepositorio()));
        });
    }
}
