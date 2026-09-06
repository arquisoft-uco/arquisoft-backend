package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoEvaluacion. */
public enum EstadoEvaluacionKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.estadoevaluacion.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstadoEvaluacionKey(String clave, int parametros) {
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
