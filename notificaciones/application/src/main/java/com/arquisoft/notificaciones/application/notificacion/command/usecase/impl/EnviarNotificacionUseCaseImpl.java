package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.notificaciones.application.notificacion.command.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.application.notificacion.command.validator.NotificacionValidator;
import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.port.out.NotificacionOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.notification.NotificationSender;
import com.arquisoft.shared.notification.exception.NotificationDeliveryException;
import com.arquisoft.shared.notification.model.NotificationMessage;
import com.arquisoft.shared.notification.model.NotificationRecipient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarNotificacionUseCaseImpl implements EnviarNotificacionUseCase {

    private final NotificacionOutputPort notificacionOutputPort;
    private final NotificacionValidator notificacionValidator;
    private final NotificationSender notificationSender;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(EnviarNotificacionCommand entrada) {
        // Un evento reentregado por el broker no debe producir un segundo correo.
        if (notificacionValidator.yaFueProcesado(entrada.eventId())) {
            logger.info(
                    catalog.obtener(NotificacionKey.LOG_EVENTO_DUPLICADO),
                    entrada.eventId());
            return;
        }

        var notificacion = NotificacionDomain.crear(
                entrada.eventId(),
                entrada.tipo(),
                entrada.destinatarioEmail(),
                entrada.asunto());

        try {
            notificationSender.enviar(NotificationMessage.textoPlano(
                    new NotificationRecipient(entrada.destinatarioNombre(), entrada.destinatarioEmail()),
                    entrada.asunto(),
                    entrada.cuerpo()));

            notificacion.marcarEnviada();
            logger.info(
                    catalog.obtener(NotificacionKey.LOG_ENVIADA),
                    entrada.eventId(),
                    entrada.destinatarioEmail());
        } catch (NotificationDeliveryException e) {
            // No se relanza a proposito: dejar caer la excepcion mandaria el mensaje a la DLQ y
            // haria rollback de la fila, perdiendo el rastro. Persistir el fallo sigue la misma
            // linea que el outbox del proyecto — el estado FALLIDA queda consultable y es el
            // insumo de un reintento posterior.
            notificacion.marcarFallida(e.getMessage());
            logger.error(
                    catalog.obtener(NotificacionKey.LOG_FALLIDA),
                    e,
                    entrada.eventId(),
                    entrada.destinatarioEmail());
        }

        notificacionOutputPort.guardar(notificacion);
    }
}
