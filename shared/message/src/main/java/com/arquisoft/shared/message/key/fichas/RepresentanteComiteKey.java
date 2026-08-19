package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RepresentanteComite. */
public enum RepresentanteComiteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.representantecomite.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    RepresentanteComiteKey(String clave, int parametros) {
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
