package com.arquisoft.notificaciones.domain.notificacion.model;

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

    /** Un estado es terminal cuando ya se resolvio el intento de entrega. */
    public boolean esTerminal() {
        return this == ENVIADA || this == FALLIDA;
    }
}
