package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.ActualizarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.ActualizarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.infrastructure.config.SolicitudesUsuariosQueueConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class UsuarioModificadoConsumer extends AbstractEventConsumer {

    private final ActualizarUsuarioInteractor actualizarUsuarioInteractor;
    private final AppLogger logger;

    public UsuarioModificadoConsumer(
            ActualizarUsuarioInteractor actualizarUsuarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.actualizarUsuarioInteractor = actualizarUsuarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.USUARIO_MODIFICADO_QUEUE)
    public void onUsuarioModificado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioModificadoPayload.class);

            logger.info(UsuarioReplicaKey.LOG_USUARIO_MODIFICADO_RECIBIDO,
                    payload.idEvento(), payload.usuario());

            var resultado = actualizarUsuarioInteractor.ejecutar(ActualizarUsuarioCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case ActualizacionUsuarioResult.Actualizada actualizada ->
                        logger.info(UsuarioReplicaKey.LOG_ACTUALIZADO, actualizada.usuario());
                case ActualizacionUsuarioResult.Descartada descartada ->
                        logger.info(UsuarioReplicaKey.LOG_ACTUALIZACION_DESCARTADA,
                                descartada.usuario(), descartada.ocurridoEnVigente(),
                                payload.ocurridoEn());
                case ActualizacionUsuarioResult.NoReplicado noReplicado ->
                        logger.debug(UsuarioReplicaKey.LOG_ACTUALIZACION_NO_REPLICADO,
                                noReplicado.usuario());
            }
        });
    }
}
