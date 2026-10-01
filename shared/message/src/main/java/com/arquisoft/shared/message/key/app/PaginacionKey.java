package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

/** Paginación de las consultas. */
public enum PaginacionKey implements ClaveMensaje {

    SIZE_MAYOR_CERO("app.aplicacion.paginacion.error.size-mayor-cero", 0);

    private final String clave;
    private final int parametros;

    PaginacionKey(String clave, int parametros) {
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
