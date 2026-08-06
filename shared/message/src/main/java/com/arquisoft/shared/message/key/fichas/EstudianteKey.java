package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Estudiante. */
public enum EstudianteKey implements MessageKey {

    ERROR_NO_ENCONTRADO("fichas.dominio.estudiante.error.no-encontrado");

    private final String clave;

    EstudianteKey(String clave) {
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
