package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Estudiante. */
public enum EstudianteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estudiante.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    EstudianteKey(String clave, int parametros) {
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
