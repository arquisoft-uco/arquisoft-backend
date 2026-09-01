package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Autenticacion. */
public enum AutenticacionKey implements ClaveMensaje {

    LOG_AUTENTICAR_DEBUG("seguridad.aplicacion.autenticacion.log.autenticar-debug", 0),
    LOG_AUTENTICAR_EXITOSO("seguridad.aplicacion.autenticacion.log.autenticar-exitoso", 0);

    private final String clave;
    private final int parametros;

    AutenticacionKey(String clave, int parametros) {
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
