package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.shared.message.key.fichas.UsuarioKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class UsuarioCreadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    public UsuarioCreadoConsumer(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            CatalogoMensajes catalogo,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.logger = logger;
        this.catalogo = catalogo;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = deserialize(message, UsuarioCreadoPayload.class);

            logger.info(catalogo.obtener(UsuarioKey.LOG_USUARIO_CREADO_RECIBIDO),
                    payload.usuarioId(), payload.email(), payload.rol());

            registrarUsuarioUseCase.ejecutar(new RegistrarUsuarioCommand(
                    UUID.fromString(payload.usuarioId()),
                    payload.email(),
                    payload.rol()));
        });
    }
}
