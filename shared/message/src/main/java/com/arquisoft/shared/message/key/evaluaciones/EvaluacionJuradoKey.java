package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionJuradoKey implements ClaveMensaje {

    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacionjurado.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacionjurado.log.consulta-completada", 3);

    private final String clave;
    private final int parametros;

    EvaluacionJuradoKey(String clave, int parametros) {
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
