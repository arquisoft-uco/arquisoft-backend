package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Reglas del agregado {@code NotificacionDomain}. */
public enum NotificacionKey implements ClaveMensaje {

    ERROR_TRANSICION_INVALIDA("notificaciones.dominio.notificacion.error.transicion-invalida", 1),
    ERROR_TIPO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.tipo-no-encontrado", 1),
    ERROR_ESTADO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.estado-no-encontrado", 1),
    LOG_EVENTO_DUPLICADO("notificaciones.aplicacion.notificacion.log.evento-duplicado", 1),
    LOG_ENVIADA("notificaciones.aplicacion.notificacion.log.enviada", 2),
    LOG_FALLIDA("notificaciones.aplicacion.notificacion.log.fallida", 2);

    private final String clave;
    private final int parametros;

    NotificacionKey(String clave, int parametros) {
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
