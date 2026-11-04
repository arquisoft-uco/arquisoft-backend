package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoRevisionAsesorKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("artefactos.aplicacion.estadorevisionasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstadoRevisionAsesorKey(String clave, int parametros) {
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
