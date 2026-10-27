package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("artefactos.dominio.estudiante.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("artefactos.dominio.estudiante.error.no-encontrado-detalle", 1),
    ERROR_NO_VIGENTE("artefactos.dominio.estudiante.error.no-vigente", 1),
    ERROR_NO_VIGENTE_DETALLE("artefactos.dominio.estudiante.error.no-vigente-detalle", 1);

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
