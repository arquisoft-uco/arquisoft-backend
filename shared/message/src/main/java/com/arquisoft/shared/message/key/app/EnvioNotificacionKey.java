package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

/** Envío de notificaciones — transversal, lo produce {@code shared:notification}. */
public enum EnvioNotificacionKey implements ClaveMensaje {

    ERROR_ENVIO_FALLIDO("app.infraestructura.notificacion.error.envio-fallido", 1);

    private final String clave;
    private final int parametros;

    EnvioNotificacionKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
