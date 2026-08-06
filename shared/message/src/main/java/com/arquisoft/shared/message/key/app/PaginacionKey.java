package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Paginación de las consultas. */
public enum PaginacionKey implements MessageKey {

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
    public String bundle() {
        return MessageBundles.APP;
    }
}
