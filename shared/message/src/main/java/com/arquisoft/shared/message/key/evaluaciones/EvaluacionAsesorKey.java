package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionAsesorKey implements ClaveMensaje {

    ERROR_SIN_EVALUACION_ASESOR("evaluaciones.dominio.evaluacionasesor.error.sin-evaluacion-asesor", 0),
    ERROR_SIN_EVALUACION_ASESOR_DETALLE(
            "evaluaciones.dominio.evaluacionasesor.error.sin-evaluacion-asesor-detalle", 1),
    ERROR_NO_DISPONIBLE("evaluaciones.infraestructura.evaluacionasesor.error.no-disponible", 0),
    ERROR_NO_DISPONIBLE_DETALLE("evaluaciones.infraestructura.evaluacionasesor.error.no-disponible-detalle", 1),
    ERROR_NO_PERTENECE_ESTUDIANTE("evaluaciones.dominio.evaluacionasesor.error.no-pertenece-estudiante", 0),
    ERROR_NO_PERTENECE_ESTUDIANTE_DETALLE(
            "evaluaciones.dominio.evaluacionasesor.error.no-pertenece-estudiante-detalle", 2),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacionasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacionasesor.log.consulta-completada", 1),
    ERROR_REALIZADA_NO_DISPONIBLE(
            "evaluaciones.infraestructura.evaluacionasesor.error.realizada-no-disponible", 0),
    ERROR_REALIZADA_NO_DISPONIBLE_DETALLE(
            "evaluaciones.infraestructura.evaluacionasesor.error.realizada-no-disponible-detalle", 1),
    LOG_CONSULTANDO_REALIZADA("evaluaciones.aplicacion.evaluacionasesor.log.consultando-realizada", 1),
    LOG_CONSULTA_REALIZADA_COMPLETADA(
            "evaluaciones.aplicacion.evaluacionasesor.log.consulta-realizada-completada", 1);

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
