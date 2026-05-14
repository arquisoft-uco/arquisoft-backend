package com.arquisoft.fichas.infrastructure.adapter.in.amqp;

import com.arquisoft.fichas.domain.port.in.RegistrarUsuarioUseCase;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import tools.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Consumer AMQP del evento {@code seguridad.usuario.creado}.
 *
 * <p>Recibe el evento publicado por el contexto {@code seguridad} cuando se crea un usuario
 * y delega al use case {@link RegistrarUsuarioUseCase} para que el contexto {@code fichas}
 * registre al usuario en su base de datos espejo.
 *
 * <p>Extiende {@link AbstractEventConsumer} para obtener:
 * <ul>
 *   <li>Propagación automática de {@code X-Trace-Id} y {@code X-User-Id} al MDC.</li>
 *   <li>ACK automático en caso de éxito.</li>
 *   <li>NACK (requeue=false) en caso de excepción — el mensaje va al DLQ.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioCreadoConsumer extends AbstractEventConsumer {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = FichasUsuariosQueueConfig.USUARIO_CREADO_QUEUE)
    public void onUsuarioCreado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            UsuarioCreadoPayload payload = objectMapper.readValue(
                    message.getBody(), UsuarioCreadoPayload.class);

            log.info("[FICHAS] UsuarioCreado recibido: usuarioId={} email={} rol={}",
                    payload.aggregateId(), payload.email(), payload.rol());

            registrarUsuarioUseCase.registrar(
                    UUID.fromString(payload.aggregateId()),
                    payload.email(),
                    payload.rol());
        });
    }
}
