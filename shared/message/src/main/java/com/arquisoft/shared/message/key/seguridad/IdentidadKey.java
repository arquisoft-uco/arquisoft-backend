package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Identidad. */
public enum IdentidadKey implements MessageKey {

    ERROR_ID_REQUERIDO("seguridad.dominio.identidad.error.id-requerido"),
    ERROR_CORREO_REQUERIDO("seguridad.dominio.identidad.error.correo-requerido");

    private final String clave;

    IdentidadKey(String clave) {
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
