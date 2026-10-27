package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionKey implements ClaveMensaje {

    ERROR_FINALIZADA("evaluaciones.dominio.evaluacion.error.finalizada", 0),
    ERROR_ESTADO_NO_ENCONTRADO("evaluaciones.dominio.evaluacion.error.estado-no-encontrado", 0),
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("evaluaciones.dominio.evaluacion.error.estado-no-encontrado-detalle", 1),
    ERROR_NO_ENCONTRADA("evaluaciones.dominio.evaluacion.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("evaluaciones.dominio.evaluacion.error.no-encontrada-detalle", 1),
    LOG_INICIADA("evaluaciones.aplicacion.evaluacion.log.iniciada", 3),
    LOG_ESTADO_ACTUALIZADO("evaluaciones.infraestructura.evaluacion.log.estado-actualizado", 2),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacion.log.consulta-completada", 3);

    private final String clave;
    private final int parametros;

    EvaluacionKey(String clave, int parametros) {
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
