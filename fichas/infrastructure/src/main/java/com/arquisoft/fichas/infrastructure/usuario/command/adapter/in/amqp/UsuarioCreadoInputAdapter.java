package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.port.in.RegistrarUsuarioInputPort;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.message.FichasMessages;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class UsuarioCreadoInputAdapter extends AbstractEventConsumer {

    private final RegistrarUsuarioInputPort registrarUsuarioInputPort;

    public UsuarioCreadoInputAdapter(
            RegistrarUsuarioInputPort registrarUsuarioInputPort,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper) {
        super(objectMapper);
        this.registrarUsuarioInputPort = registrarUsuarioInputPort;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = deserialize(message, UsuarioCreadoPayload.class);

            log.info(FichasMessages.Usuario.LOG_USUARIO_CREADO_RECIBIDO,
                    payload.usuarioId(), payload.email(), payload.rol());

            registrarUsuarioInputPort.ejecutar(new RegistrarUsuarioCommand(
                    UUID.fromString(payload.usuarioId()),
                    payload.email(),
                    payload.rol()));
        });
    }
}
