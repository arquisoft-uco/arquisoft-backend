package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.shared.message.key.fichas.UsuarioEspejoKey;
import com.arquisoft.fichas.application.usuario.command.primaryport.model.RegistrarUsuarioEspejoCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioEspejoUseCase;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import com.arquisoft.shared.util.UtilUUID;

import java.io.IOException;

@Component
public class UsuarioCreadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioEspejoUseCase registrarUsuarioEspejoUseCase;
    private final AppLogger logger;

    public UsuarioCreadoConsumer(
            RegistrarUsuarioEspejoUseCase registrarUsuarioEspejoUseCase,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarUsuarioEspejoUseCase = registrarUsuarioEspejoUseCase;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = deserialize(message, UsuarioCreadoPayload.class);

            logger.info(UsuarioEspejoKey.LOG_USUARIO_CREADO_RECIBIDO,
                    payload.usuarioId(), UtilTexto.enmascararCorreo(payload.email()), payload.rol());

            registrarUsuarioEspejoUseCase.ejecutar(new RegistrarUsuarioEspejoCommand(
                    UtilUUID.generarUUIDDesdeTexto(payload.usuarioId()),
                    payload.email(),
                    payload.rol()));
        });
    }
}
