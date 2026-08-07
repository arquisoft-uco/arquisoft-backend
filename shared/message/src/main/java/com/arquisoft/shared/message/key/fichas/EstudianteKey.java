package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Estudiante. */
public enum EstudianteKey implements ClaveMensaje {

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
    public String paquete() {
        return PaquetesMensajes.FICHAS;
    }
}
