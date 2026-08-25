package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Credenciales. */
public enum CredencialesKey implements ClaveMensaje {

    ERROR_TOKEN_ACCESO_REQUERIDO("seguridad.dominio.credenciales.error.token-acceso-requerido", 0),
    ERROR_EXPIRACION_INVALIDA("seguridad.dominio.credenciales.error.expiracion-invalida", 0),
    ERROR_TIPO_TOKEN_REQUERIDO("seguridad.dominio.credenciales.error.tipo-token-requerido", 0);

    private final String clave;
    private final int parametros;

    CredencialesKey(String clave, int parametros) {
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
