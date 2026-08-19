package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.application.notificacion.command.validator.NotificacionValidator;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.notification.EnvioNotificacionOutputPort;
import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import com.arquisoft.shared.notification.model.DestinatarioNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarNotificacionUseCaseImpl implements EnviarNotificacionUseCase {

    private final NotificacionOutputPort notificacionOutputPort;
    private final NotificacionValidator notificacionValidator;
    private final EnvioNotificacionOutputPort envioNotificacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EnviarNotificacionCommand entrada) {
        // Un evento reentregado por el broker no debe producir un segundo correo.
        if (notificacionValidator.yaFueProcesado(entrada.idEvento())) {
            logger.info(
                    Mensajes.obtener(NotificacionKey.LOG_EVENTO_DUPLICADO),
                    entrada.idEvento());
            return;
        }

        var notificacion = NotificacionDomain.crear(
                entrada.idEvento(),
                entrada.tipo(),
                entrada.destinatarioEmail(),
                entrada.asunto());

        try {
            envioNotificacionOutputPort.enviar(MensajeNotificacion.textoPlano(
                    new DestinatarioNotificacion(entrada.destinatarioNombre(), entrada.destinatarioEmail()),
                    entrada.asunto(),
                    entrada.cuerpo()));

            notificacion.marcarEnviada();
            logger.info(
                    Mensajes.obtener(NotificacionKey.LOG_ENVIADA),
                    entrada.idEvento(),
                    entrada.destinatarioEmail());
        } catch (EnvioNotificacionFallidoException e) {
            // No se relanza a proposito: dejar caer la excepcion mandaria el mensaje a la DLQ y
            // haria rollback de la fila, perdiendo el rastro. Persistir el fallo sigue la misma
            // linea que el outbox del proyecto — el estado FALLIDA queda consultable y es el
            // insumo de un reintento posterior.
            notificacion.marcarFallida(e.getMessage());
            logger.error(
                    Mensajes.obtener(NotificacionKey.LOG_FALLIDA),
                    e,
                    entrada.idEvento(),
                    entrada.destinatarioEmail());
        }

        notificacionOutputPort.guardar(NotificacionMapper.toEntity(notificacion));
    }
}
