package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Autenticacion. */
public enum AutenticacionKey implements ClaveMensaje {

    LOG_AUTENTICAR_DEBUG("seguridad.aplicacion.autenticacion.log.autenticar-debug"),
    LOG_AUTENTICAR_EXITOSO("seguridad.aplicacion.autenticacion.log.autenticar-exitoso"),
    LOG_REFRESCO_EXITOSO("seguridad.aplicacion.autenticacion.log.refresco-exitoso"),
    LOG_VALIDAR_DEBUG("seguridad.aplicacion.autenticacion.log.validar-debug");

    private final String clave;

    AutenticacionKey(String clave) {
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
