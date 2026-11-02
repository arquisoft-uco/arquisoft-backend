package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionAsesorKey implements ClaveMensaje {

    ERROR_SIN_EVALUACION_ASESOR("evaluaciones.dominio.evaluacionasesor.error.sin-evaluacion-asesor", 0),
    ERROR_SIN_EVALUACION_ASESOR_DETALLE(
            "evaluaciones.dominio.evaluacionasesor.error.sin-evaluacion-asesor-detalle", 1),
    ERROR_NO_DISPONIBLE("evaluaciones.infraestructura.evaluacionasesor.error.no-disponible", 0),
    ERROR_NO_DISPONIBLE_DETALLE("evaluaciones.infraestructura.evaluacionasesor.error.no-disponible-detalle", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacionasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacionasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionAsesorKey(String clave, int parametros) {
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
