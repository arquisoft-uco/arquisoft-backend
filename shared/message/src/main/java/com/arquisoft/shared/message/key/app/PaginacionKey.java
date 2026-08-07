package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Paginación de las consultas. */
public enum PaginacionKey implements ClaveMensaje {

    SIZE_MAYOR_CERO("app.aplicacion.paginacion.error.size-mayor-cero");

    private final String clave;

    PaginacionKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
