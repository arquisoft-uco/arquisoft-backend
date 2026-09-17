package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoEvaluacionKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.estadoevaluacion.log.consulta-completada", 1);

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
