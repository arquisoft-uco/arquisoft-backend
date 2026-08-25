package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Autenticacion. */
public enum AutenticacionKey implements ClaveMensaje {

    ERROR_CORREO_REQUERIDO("seguridad.dominio.autenticacion.error.correo-requerido", 0),
    ERROR_CORREO_INVALIDO("seguridad.dominio.autenticacion.error.correo-invalido", 0),
    ERROR_CLAVE_REQUERIDA("seguridad.dominio.autenticacion.error.clave-requerida", 0),
    ERROR_CLAVE_DEMASIADO_CORTA("seguridad.dominio.autenticacion.error.clave-demasiado-corta", 1),
    LOG_AUTENTICAR_DEBUG("seguridad.aplicacion.autenticacion.log.autenticar-debug", 0),
    LOG_AUTENTICAR_EXITOSO("seguridad.aplicacion.autenticacion.log.autenticar-exitoso", 0),
    LOG_REFRESCO_EXITOSO("seguridad.aplicacion.autenticacion.log.refresco-exitoso", 0),
    LOG_VALIDAR_DEBUG("seguridad.aplicacion.autenticacion.log.validar-debug", 0);

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
