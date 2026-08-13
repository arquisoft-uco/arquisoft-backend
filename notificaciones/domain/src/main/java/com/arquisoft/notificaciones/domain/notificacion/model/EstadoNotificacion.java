package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.EstadoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

/**
 * Ciclo de vida de una notificacion.
 *
 * <p>{@code PENDIENTE} se registra antes de intentar la entrega: si el proceso muere a mitad de
 * camino queda el rastro de que el evento ya se estaba atendiendo.
 */
public enum EstadoNotificacion {

    PENDIENTE,
    ENVIADA,
    FALLIDA;

    public String getId() {
        return name();
    }

    /** Un estado es terminal cuando ya se resolvio el intento de entrega. */
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
