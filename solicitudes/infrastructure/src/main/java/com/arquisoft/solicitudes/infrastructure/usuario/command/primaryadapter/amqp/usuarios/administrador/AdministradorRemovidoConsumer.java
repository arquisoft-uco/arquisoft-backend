package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.administrador;

import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.solicitudes.infrastructure.config.SolicitudesUsuariosQueueConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AdministradorRemovidoConsumer extends AbstractEventConsumer {

    private final AppLogger logger;

    public AdministradorRemovidoConsumer(
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.ADMINISTRADOR_REMOVIDO_QUEUE)
    public void onAdministradorRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AdministradorRemovidoPayload.class);

            logger.info(UsuarioReplicaKey.LOG_ADMINISTRADOR_REMOVIDO_RECIBIDO_STUB,
                    payload.idEvento(), payload.usuario());
            // TODO (stub deliberado de HU-232): implementar la baja/actualizacion real de la replica de administrador en
            //  solicitudes cuando se defina el caso de uso (hoy la replica de usuario es generica y
            //  no distingue el rol administrador). Ver AdministradorRemovidoEvent (usuarios).
        });
    }
}
