package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoFicha. */
public enum EstadoFichaKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estadoficha.error.no-encontrado");

    private final String clave;

    EstadoFichaKey(String clave) {
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
