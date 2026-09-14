package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.coordinador;

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

@Component
public class CoordinadorAgregadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioInteractor registrarUsuarioInteractor;
    private final AppLogger logger;

    public CoordinadorAgregadoConsumer(
            RegistrarUsuarioInteractor registrarUsuarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.registrarUsuarioInteractor = registrarUsuarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = SolicitudesUsuariosQueueConfig.COORDINADOR_AGREGADO_QUEUE)
    public void onCoordinadorAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, CoordinadorAgregadoPayload.class);

            logger.info(UsuarioReplicaKey.LOG_USUARIO_AGREGADO_RECIBIDO,
                    payload.usuario(), payload.identificador(), payload.nombre(),
                    UtilTexto.enmascararCorreo(payload.email()));

            registrarUsuarioInteractor.ejecutar(new RegistrarUsuarioCommand(
                    UUID.fromString(payload.usuario()),
                    payload.identificador(),
                    payload.nombre(),
                    payload.email()));
        });
    }
}
