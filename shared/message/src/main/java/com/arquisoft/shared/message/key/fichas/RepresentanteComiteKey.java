package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RepresentanteComite. */
public enum RepresentanteComiteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.representantecomite.error.no-encontrado");

    private final String clave;

    RepresentanteComiteKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.FICHAS;
    }
}
