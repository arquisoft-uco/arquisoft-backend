package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionesUsuariosQueueConfig;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.AbstractNotificacionConsumer;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.TipoNotificacionEvento;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class UsuarioEstadoCambiadoConsumer extends AbstractNotificacionConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;

    public UsuarioEstadoCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
    }

    @RabbitListener(queues = NotificacionesUsuariosQueueConfig.ESTADO_CAMBIADO_QUEUE)
    public void onUsuarioEstadoCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, UsuarioEstadoCambiadoPayload.class);

            logger.info(
                    ConsumidorKey.LOG_USUARIO_ESTADO_CAMBIADO_RECIBIDO,
                    payload.usuario(),
                    payload.estado(),
                    UtilTexto.enmascararCorreo(payload.email()));

            registrar(enviarNotificacionInteractor.ejecutar(EnviarNotificacionCommand.crear(
                    payload.idEvento(),
                    TipoNotificacionEvento.USUARIO_ESTADO_CAMBIADO.getCodigo(),
                    payload.nombre(),
                    payload.email(),
                    plantilla(
                            PlantillaKey.ASUNTO_USUARIO_ESTADO_CAMBIADO,
                            payload.estadoNombre()),
                    plantilla(
                            PlantillaKey.CUERPO_USUARIO_ESTADO_CAMBIADO,
                            payload.nombre(),
                            payload.estadoNombre()),
                    plantilla(PlantillaKey.PIE_GENERICO))));
        });
    }
}
