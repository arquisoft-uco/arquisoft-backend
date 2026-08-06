package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de RepresentanteComite. */
public enum RepresentanteComiteKey implements MessageKey {

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
    public String bundle() {
        return MessageBundles.FICHAS;
    }
}
