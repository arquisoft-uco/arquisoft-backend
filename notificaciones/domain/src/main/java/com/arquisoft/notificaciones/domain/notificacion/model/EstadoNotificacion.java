package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.EstadoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

public enum EstadoNotificacion {

    PENDIENTE,
    ENVIADA,
    FALLIDA;

    public String getId() {
        return name();
    }

    public boolean esTerminal() {
        return this == ENVIADA || this == FALLIDA;
    }

    public static EstadoNotificacion desde(String id) {
        return UtilEnum.desde(EstadoNotificacion.class, id)
                .orElseThrow(() -> new EstadoNotificacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return UtilEnum.esValido(EstadoNotificacion.class, id);
    }
}
