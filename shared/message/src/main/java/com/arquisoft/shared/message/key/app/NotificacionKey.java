package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Envío de notificaciones — transversal, lo produce {@code shared:notification}. */
public enum NotificacionKey implements ClaveMensaje {

    ERROR_ENVIO_FALLIDO("app.infraestructura.notificacion.error.envio-fallido");

    private final String clave;

    NotificacionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
