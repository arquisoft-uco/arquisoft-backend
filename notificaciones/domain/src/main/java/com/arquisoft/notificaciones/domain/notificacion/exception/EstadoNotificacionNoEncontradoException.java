package com.arquisoft.notificaciones.domain.notificacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;

public final class EstadoNotificacionNoEncontradoException extends DomainException {

    public EstadoNotificacionNoEncontradoException(String id) {
        super(
                Mensajes.formatear(NotificacionKey.ERROR_ESTADO_NO_ENCONTRADO, id),
                NotificacionesCodes.Notificacion.ESTADO_NO_ENCONTRADO
        );
    }
}
