package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionEvaluacionKey implements ClaveMensaje {

    ERROR_OBSERVACION_EVALUACION_DUPLICADA("fichas.dominio.observacionevaluacion.error.duplicada", 2),
    ERROR_EVALUACION_CERRADA("fichas.dominio.observacionevaluacion.error.evaluacion-cerrada", 2),
    LOG_AGREGANDO("fichas.aplicacion.observacionevaluacion.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.observacionevaluacion.log.verificacion-agregar", 4),
    LOG_AGREGADA("fichas.aplicacion.observacionevaluacion.log.agregada", 2),
    LOG_GUARDADA("fichas.infraestructura.observacionevaluacion.log.guardada", 1);

    private final String clave;
    private final int parametros;

    ObservacionEvaluacionKey(String clave, int parametros) {
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
