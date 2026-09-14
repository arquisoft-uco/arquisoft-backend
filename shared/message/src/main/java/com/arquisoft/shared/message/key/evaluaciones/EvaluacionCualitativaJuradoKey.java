package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCualitativaJuradoKey implements ClaveMensaje {

    ERROR_EVALUACION_JURADO_NO_ENCONTRADA(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.evaluacion-jurado-no-encontrada", 1),
    ERROR_EVALUACION_JURADO_NO_PERTENECE(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.evaluacion-jurado-no-pertenece", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncualitativajurado.log.consultando", 2),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncualitativajurado.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCualitativaJuradoKey(String clave, int parametros) {
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
