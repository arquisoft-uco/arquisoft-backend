package com.arquisoft.fichas.infrastructure.adapter.in.amqp;

import com.arquisoft.fichas.infrastructure.config.FichasSeguridadQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Adaptador de entrada AMQP — escucha el evento {@code seguridad.usuario.autenticado}.
 *
 * <p>Recibe el evento publicado por el contexto de seguridad cuando un usuario se autentica.
 * Actualmente registra el evento; en el futuro invocará el caso de uso de inicialización
 * de fichas de perfil para ese usuario.
 *
 * <p>Ubicado en {@code adapter/in/amqp/} conforme a la arquitectura hexagonal:
 * los adaptadores de entrada reciben mensajes externos y los traducen a llamadas de dominio.
 */
@Slf4j
@Component
public class UsuarioAutenticadoConsumer extends AbstractEventConsumer {

    @RabbitListener(queues = FichasSeguridadQueueConfig.USUARIO_AUTENTICADO_QUEUE)
    public void onUsuarioAutenticado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            String body = new String(message.getBody());
            log.info("Evento recibido [seguridad.usuario.autenticado]: {}", body);
            // TODO: invocar caso de uso de fichas para inicializar perfil del usuario
        });
    }
}
