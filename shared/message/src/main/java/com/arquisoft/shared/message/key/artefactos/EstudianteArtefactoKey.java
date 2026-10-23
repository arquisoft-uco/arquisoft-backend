package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteArtefactoKey implements ClaveMensaje {

    LOG_CONSULTANDO("artefactos.aplicacion.estudianteartefacto.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("artefactos.aplicacion.estudianteartefacto.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstudianteArtefactoKey(String clave, int parametros) {
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
