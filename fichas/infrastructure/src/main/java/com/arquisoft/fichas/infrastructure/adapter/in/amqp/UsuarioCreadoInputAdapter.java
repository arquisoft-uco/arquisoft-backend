package com.arquisoft.fichas.infrastructure.adapter.in.amqp;

import com.arquisoft.fichas.application.fichaperfil.command.RegistrarUsuarioInputPort;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Input Adapter AMQP del evento {@code seguridad.usuario.creado}.
 *
 * <p>Recibe el evento publicado por el contexto {@code seguridad} cuando se crea un usuario
 * y delega al use case {@link RegistrarUsuarioInputPort} para que el contexto {@code fichas}
 * registre al usuario en su base de datos espejo.
 *
 * <p>Extiende {@link AbstractEventConsumer} para obtener:
 * <ul>
 *   <li>Propagación automática de {@code X-Trace-Id} y {@code X-User-Id} al MDC.</li>
 *   <li>ACK automático en caso de éxito.</li>
 *   <li>NACK (requeue=false) en caso de excepción — el mensaje va al DLQ.</li>
 *   <li>Deserialización centralizada vía {@link AbstractEventConsumer#deserialize}.</li>
 * </ul>
 */
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

            log.info("[FICHAS] UsuarioCreado recibido: usuarioId={} email={} rol={}",
                    payload.aggregateId(), payload.email(), payload.rol());

            registrarUsuarioInputPort.registrar(
                    UUID.fromString(payload.aggregateId()),
                    payload.email(),
                    payload.rol());
        });
    }
}
