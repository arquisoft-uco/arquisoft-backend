package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.logging;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notificacion.proveedor", havingValue = "log", matchIfMissing = true)
public class LogEnvioNotificacionOutputAdapter implements EnvioNotificacionOutputPort {

    private final AppLogger logger;

    @Override
    public ResultadoEntrega enviar(MensajeNotificacion mensaje) {
        logger.info(
                Mensajes.obtener(EnvioNotificacionKey.LOG_ENVIO_SIMULADO),
                mensaje.destinatarios().stream()
                        .map(DestinatarioNotificacion::email)
                        .map(UtilTexto::enmascararCorreo)
                        .toList(),
                mensaje.asunto());
        return new ResultadoEntrega.Entregada();
    }
}
