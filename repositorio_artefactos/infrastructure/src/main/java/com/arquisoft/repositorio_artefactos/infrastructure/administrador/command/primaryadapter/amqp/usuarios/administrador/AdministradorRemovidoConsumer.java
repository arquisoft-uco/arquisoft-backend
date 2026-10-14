package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.administrador;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.RemoverAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AdministradorRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverAdministradorInteractor removerAdministradorInteractor;
    private final AppLogger logger;

    public AdministradorRemovidoConsumer(
            RemoverAdministradorInteractor removerAdministradorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerAdministradorInteractor = removerAdministradorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = RepositorioArtefactosUsuariosQueueConfig.ADMINISTRADOR_REMOVIDO_QUEUE)
    public void onAdministradorRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AdministradorRemovidoPayload.class);

            logger.info(AdministradorKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerAdministradorInteractor.ejecutar(RemoverAdministradorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionAdministradorResult.Removida removida ->
                        logger.info(AdministradorKey.LOG_REMOVIDO, removida.administrador());
                case RemocionAdministradorResult.Lapida lapida ->
                        logger.info(AdministradorKey.LOG_LAPIDA, lapida.administrador());
                case RemocionAdministradorResult.Descartada descartada ->
                        logger.debug(AdministradorKey.LOG_REMOCION_DESCARTADA,
                                descartada.administrador(), descartada.ocurridoEnVigente());
            }
        });
    }
}
