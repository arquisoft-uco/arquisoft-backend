package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp;

import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaNotificacionNoDisponibleException;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import tools.jackson.databind.ObjectMapper;

public abstract class AbstractNotificacionConsumer extends AbstractEventConsumer {

    protected final AppLogger logger;

    protected AbstractNotificacionConsumer(
            ObjectMapper objectMapper, GestorTraza gestorTraza, AppLogger logger) {
        super(objectMapper, gestorTraza);
        this.logger = logger;
    }

    protected String plantilla(ClaveMensaje clave, Object... args) {
        if (!Mensajes.catalogo().contiene(clave)) {
            throw new PlantillaNotificacionNoDisponibleException(clave);
        }
        return Mensajes.formatear(clave, args);
    }

    protected void registrar(EnvioNotificacionResult resultado) {
        switch (resultado) {
            case EnvioNotificacionResult.Enviada enviada -> logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_NOTIFICACION_ENVIADA),
                    enviada.idEvento(),
                    UtilTexto.enmascararCorreo(enviada.destinatario()));
            case EnvioNotificacionResult.Duplicada duplicada -> logger.info(
                    Mensajes.obtener(ConsumidorKey.LOG_NOTIFICACION_DUPLICADA),
                    duplicada.idEvento());
            case EnvioNotificacionResult.Fallida fallida -> logger.warn(
                    Mensajes.obtener(ConsumidorKey.LOG_NOTIFICACION_FALLIDA),
                    fallida.idEvento(),
                    UtilTexto.enmascararCorreo(fallida.destinatario()));
        }
    }
}
