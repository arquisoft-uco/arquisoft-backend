package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Autenticacion. */
public enum AutenticacionKey implements MessageKey {

    LOG_AUTENTICAR_DEBUG("seguridad.aplicacion.autenticacion.log.autenticar-debug"),
    LOG_AUTENTICAR_EXITOSO("seguridad.aplicacion.autenticacion.log.autenticar-exitoso"),
    LOG_REFRESH_EXITOSO("seguridad.aplicacion.autenticacion.log.refresh-exitoso"),
    LOG_VALIDATE_DEBUG("seguridad.aplicacion.autenticacion.log.validate-debug");

    private final String clave;

    AutenticacionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.SEGURIDAD;
    }
}
