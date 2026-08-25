package com.arquisoft.shared.notification.logging;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.notification.EnvioNotificacionOutputPort;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import com.arquisoft.shared.notification.model.DestinatarioNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Estrategia de desarrollo: registra la notificacion en el log y no la entrega a nadie.
 *
 * <p>Es la activa por defecto ({@code matchIfMissing = true}). La eleccion es deliberada: un
 * entorno mal configurado, o unas pruebas que levanten el contexto sin definir la propiedad,
 * nunca terminan enviando correo real a una persona.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notificacion.proveedor", havingValue = "log", matchIfMissing = true)
public class LogEnvioNotificacionOutputAdapter implements EnvioNotificacionOutputPort {

    private static final String LOG_ENVIO_SIMULADO =
            "[NOTIFICACION:LOG] Envio simulado — destinatarios={} asunto={}";

    private final AppLogger logger;

    @Override
    public void enviar(MensajeNotificacion mensaje) {
        logger.info(
                LOG_ENVIO_SIMULADO,
                mensaje.destinatarios().stream().map(DestinatarioNotificacion::email).toList(),
                mensaje.asunto());
    }
}
