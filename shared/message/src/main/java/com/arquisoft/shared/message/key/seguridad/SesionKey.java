package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Sesion. */
public enum SesionKey implements ClaveMensaje {

    ERROR_IDENTIFICADOR_REQUERIDO("seguridad.dominio.sesion.error.identificador-requerido"),
    ERROR_TTL_INVALIDO("seguridad.dominio.sesion.error.ttl-invalido"),
    LOG_LOGOUT_EXITOSO("seguridad.aplicacion.sesion.log.logout-exitoso");

    private final String clave;

    SesionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.SEGURIDAD;
    }
}
