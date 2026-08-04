package com.arquisoft.shared.notification.exception;

import com.arquisoft.shared.exception.InfrastructureException;

/**
 * El proveedor de notificaciones rechazo el envio o no esta disponible.
 *
 * <p>Extiende {@code InfrastructureException} para que {@code GlobalAppExceptionHandler} la
 * resuelva como 503: un fallo de entrega es indisponibilidad de un tercero, no un error de datos.
 */
public class NotificationDeliveryException extends InfrastructureException {

    public NotificationDeliveryException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
