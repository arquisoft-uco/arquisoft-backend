package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Identidad. */
public enum IdentidadKey implements ClaveMensaje {

    ERROR_ID_REQUERIDO("seguridad.dominio.identidad.error.id-requerido", 0),
    ERROR_CORREO_REQUERIDO("seguridad.dominio.identidad.error.correo-requerido", 0);

    private final String clave;
    private final int parametros;

    IdentidadKey(String clave, int parametros) {
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
