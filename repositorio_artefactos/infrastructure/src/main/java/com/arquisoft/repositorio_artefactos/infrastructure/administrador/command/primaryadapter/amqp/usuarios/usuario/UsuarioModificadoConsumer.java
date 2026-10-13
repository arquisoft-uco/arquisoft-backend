package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.ActualizarAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.ActualizarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
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
public class UsuarioModificadoConsumer extends AbstractEventConsumer {

    private final ActualizarAdministradorInteractor actualizarAdministradorInteractor;
    private final AppLogger logger;

    public UsuarioModificadoConsumer(
            ActualizarAdministradorInteractor actualizarAdministradorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.actualizarAdministradorInteractor = actualizarAdministradorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = RepositorioArtefactosUsuariosQueueConfig.USUARIO_MODIFICADO_QUEUE)
    public void onUsuarioModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioModificadoPayload.class);

            logger.info(AdministradorKey.LOG_USUARIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.usuario());

            var resultado = actualizarAdministradorInteractor.ejecutar(ActualizarAdministradorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case ActualizacionAdministradorResult.Actualizada actualizada ->
                        logger.info(AdministradorKey.LOG_ACTUALIZADO, actualizada.administrador());
                case ActualizacionAdministradorResult.Descartada descartada ->
                        logger.info(AdministradorKey.LOG_ACTUALIZACION_DESCARTADA, descartada.administrador(),
                                descartada.ocurridoEnVigente(), payload.ocurridoEn());
                case ActualizacionAdministradorResult.NoReplicado noReplicado ->
                        logger.debug(AdministradorKey.LOG_ACTUALIZACION_NO_REPLICADO,
                                noReplicado.administrador());
            }
        });
    }
}
