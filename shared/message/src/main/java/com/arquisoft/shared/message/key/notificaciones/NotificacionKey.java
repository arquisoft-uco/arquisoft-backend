package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Reglas del agregado {@code NotificacionDomain}. */
public enum NotificacionKey implements MessageKey {

    ERROR_TRANSICION_INVALIDA("notificaciones.dominio.notificacion.error.transicion-invalida"),
    LOG_EVENTO_DUPLICADO("notificaciones.aplicacion.notificacion.log.evento-duplicado"),
    LOG_ENVIADA("notificaciones.aplicacion.notificacion.log.enviada"),
    LOG_FALLIDA("notificaciones.aplicacion.notificacion.log.fallida");

    private final String clave;

    NotificacionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.NOTIFICACIONES;
    }
}
