package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

import com.arquisoft.shared.message.key.fichas.UsuarioKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class UsuarioCreadoInputAdapter extends AbstractEventConsumer {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    public UsuarioCreadoInputAdapter(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            MessageCatalog catalog) {
        super(objectMapper);
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.logger = logger;
        this.catalog = catalog;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = deserialize(message, UsuarioCreadoPayload.class);

            logger.info(catalog.obtener(UsuarioKey.LOG_USUARIO_CREADO_RECIBIDO),
                    payload.usuarioId(), payload.email(), payload.rol());

            registrarUsuarioUseCase.ejecutar(new RegistrarUsuarioCommand(
                    UUID.fromString(payload.usuarioId()),
                    payload.email(),
                    payload.rol()));
        });
    }
}
