package com.arquisoft.notificaciones.domain.notificacion.model;

/**
 * Motivo por el que se notifica.
 *
 * <p>Es un enum del contexto, no una copia del {@code eventType} del productor: si manana
 * {@code fichas} renombra su evento, aqui solo cambia el mapeo del consumidor.
 */
public enum TipoNotificacion {

    ASESOR_FICHA_CAMBIADO;

    public String getCodigo() {
        return name();
    }
}
