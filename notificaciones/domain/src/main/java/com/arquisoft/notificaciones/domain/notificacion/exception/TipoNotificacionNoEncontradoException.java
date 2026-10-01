package com.arquisoft.notificaciones.domain.notificacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;

public final class TipoNotificacionNoEncontradoException extends DomainException {

    public TipoNotificacionNoEncontradoException(String id) {
        super(
                Mensajes.formatear(NotificacionKey.ERROR_TIPO_NO_ENCONTRADO, id),
                NotificacionesCodes.Notificacion.TIPO_NO_ENCONTRADO
        );
    }
}
