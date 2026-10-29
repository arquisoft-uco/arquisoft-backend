package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ArtefactoEntregableKey implements ClaveMensaje {

    LOG_CONSULTANDO("entregables.aplicacion.artefactoentregable.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("entregables.aplicacion.artefactoentregable.log.consulta-completada", 2);

    private final String clave;
    private final int parametros;

    ArtefactoEntregableKey(String clave, int parametros) {
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
