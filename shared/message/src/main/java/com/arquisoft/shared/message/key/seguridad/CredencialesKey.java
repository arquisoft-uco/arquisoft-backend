package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Credenciales. */
public enum CredencialesKey implements ClaveMensaje {

    ERROR_TOKEN_ACCESO_REQUERIDO("seguridad.dominio.credenciales.error.token-acceso-requerido"),
    ERROR_EXPIRACION_INVALIDA("seguridad.dominio.credenciales.error.expiracion-invalida"),
    ERROR_TIPO_TOKEN_REQUERIDO("seguridad.dominio.credenciales.error.tipo-token-requerido");

    private final String clave;

    CredencialesKey(String clave) {
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
