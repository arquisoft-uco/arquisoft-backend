package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.asesor;

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
public class AsesorRemovidoConsumer extends AbstractEventConsumer {

    private final AppLogger logger;

    public AsesorRemovidoConsumer(
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.ASESOR_REMOVIDO_QUEUE)
    public void onAsesorRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorRemovidoPayload.class);

            logger.info(UsuarioReplicaKey.LOG_ASESOR_REMOVIDO_RECIBIDO_STUB,
                    payload.idEvento(), payload.usuario());
        });
    }
}
