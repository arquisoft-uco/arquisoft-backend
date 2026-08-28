package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionProcesadaFinder;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import com.arquisoft.shared.notification.EnvioNotificacionOutputPort;
import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.DestinatarioNotificacion;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarNotificacionUseCaseImpl implements EnviarNotificacionUseCase {

    private final NotificacionOutputPort notificacionOutputPort;
    private final NotificacionProcesadaFinder notificacionProcesadaFinder;
    private final EnvioNotificacionOutputPort envioNotificacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EnviarNotificacionCommand entrada) {
        if (notificacionProcesadaFinder.obtener(entrada.idEvento())) {
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
                    UtilTexto.enmascararCorreo(entrada.destinatarioEmail()));
        } catch (EnvioNotificacionFallidoException e) {
            notificacion.marcarFallida(e.getMessage());
            logger.error(
                    Mensajes.obtener(NotificacionKey.LOG_FALLIDA),
                    e,
                    entrada.idEvento(),
                    UtilTexto.enmascararCorreo(entrada.destinatarioEmail()));
        }

        notificacionOutputPort.guardar(NotificacionMapper.toEntity(notificacion));
    }
}
