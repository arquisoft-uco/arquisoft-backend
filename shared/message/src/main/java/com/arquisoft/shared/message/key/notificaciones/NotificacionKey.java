package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Reglas del agregado {@code NotificacionDomain}. */
public enum NotificacionKey implements ClaveMensaje {

    ERROR_TRANSICION_INVALIDA("notificaciones.dominio.notificacion.error.transicion-invalida"),
    ERROR_TIPO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.tipo-no-encontrado"),
    ERROR_ESTADO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.estado-no-encontrado"),
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
    public String paquete() {
        return PaquetesMensajes.NOTIFICACIONES;
    }
}
