package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.infrastructure.config.SolicitudesUsuariosQueueConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

// Inerte hasta que 'usuarios' enriquezca UsuarioCreadoEvent (PLAN-HU-081 P1 / sección 4.4):
// si el payload llega sin identificador/nombre, ACK sin persistir.
@Component
public class UsuarioCreadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioInteractor registrarUsuarioInteractor;
    private final AppLogger logger;

    public UsuarioCreadoConsumer(
            RegistrarUsuarioInteractor registrarUsuarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarUsuarioInteractor = registrarUsuarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = deserialize(message, UsuarioCreadoPayload.class);

            if (UtilTexto.esVacioONulo(payload.identificador()) || UtilTexto.esVacioONulo(payload.nombre())) {
                logger.info(UsuarioReplicaKey.LOG_USUARIO_CREADO_IGNORADO_SIN_DATOS, payload.usuarioId());
                return;
            }

            logger.info(UsuarioReplicaKey.LOG_USUARIO_CREADO_RECIBIDO,
                    payload.usuarioId(), payload.identificador(), payload.nombre(), payload.email());

            registrarUsuarioInteractor.ejecutar(new RegistrarUsuarioCommand(
                    UUID.fromString(payload.usuarioId()),
                    payload.identificador(),
                    payload.nombre(),
                    payload.email()));
        });
    }
}
