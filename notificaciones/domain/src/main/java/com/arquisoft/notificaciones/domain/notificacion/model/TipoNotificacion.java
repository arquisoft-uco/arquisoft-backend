package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.TipoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

/**
 * Motivo por el que se notifica.
 *
 * <p>Es un enum del contexto, no una copia del {@code eventType} del productor: si manana
 * {@code fichas} renombra su evento, aqui solo cambia el mapeo del consumidor.
 */
public enum TipoNotificacion {

    ASESOR_FICHA_CAMBIADO;

    public String getId() {
        return name();
    }

    public static TipoNotificacion desde(String id) {
        return UtilEnum.desde(TipoNotificacion.class, id)
                .orElseThrow(() -> new TipoNotificacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return UtilEnum.esValido(TipoNotificacion.class, id);
    }
}
