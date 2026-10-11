package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoProyectoGrado. */
public enum EstadoProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("proyectos.dominio.estadoproyectogrado.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    EstadoProyectoGradoKey(String clave, int parametros) {
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
