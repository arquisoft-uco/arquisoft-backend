package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCualitativaAsesorKey implements ClaveMensaje {

    ERROR_EVALUACION_ASESOR_NO_ENCONTRADA(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.evaluacion-asesor-no-encontrada", 1),
    ERROR_NO_PERTENECE_ASESOR(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.no-pertenece-asesor", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCualitativaAsesorKey(String clave, int parametros) {
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
