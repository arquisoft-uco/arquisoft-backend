package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoAsesor. */
public enum EstadoAsesorKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("proyectos.dominio.estadoasesor.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    EstadoAsesorKey(String clave, int parametros) {
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
