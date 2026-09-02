package com.arquisoft.notificaciones.infrastructure.notificacion.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;

public final class PlantillaCorreoNoDisponibleException extends InfrastructureException {

    public PlantillaCorreoNoDisponibleException(String detalle) {
        super(mensaje(detalle), NotificacionesCodes.Notificacion.PLANTILLA_CORREO_NO_DISPONIBLE);
    }

    public PlantillaCorreoNoDisponibleException(String detalle, Throwable causa) {
        super(mensaje(detalle),
                NotificacionesCodes.Notificacion.PLANTILLA_CORREO_NO_DISPONIBLE, causa);
    }

    private static String mensaje(String detalle) {
        return Mensajes.formatear(EnvioNotificacionKey.ERROR_PLANTILLA_CORREO, detalle);
    }
}
