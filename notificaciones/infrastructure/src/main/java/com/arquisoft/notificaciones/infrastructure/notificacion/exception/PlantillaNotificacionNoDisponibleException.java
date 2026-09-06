package com.arquisoft.notificaciones.infrastructure.notificacion.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;

public final class PlantillaNotificacionNoDisponibleException extends InfrastructureException {

    public PlantillaNotificacionNoDisponibleException(ClaveMensaje plantilla) {
        super(Mensajes.formatear(
                        ConsumidorKey.ERROR_PLANTILLA_NO_DISPONIBLE, plantilla.clave()),
                NotificacionesCodes.Notificacion.PLANTILLA_NO_DISPONIBLE);
    }
}
