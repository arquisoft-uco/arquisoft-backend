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
    ERROR_CON_EVALUACIONES("evaluaciones.dominio.evaluacionasesor.error.con-evaluaciones", 0),
    ERROR_CON_EVALUACIONES_DETALLE("evaluaciones.dominio.evaluacionasesor.error.con-evaluaciones-detalle", 2),
    LOG_ASIGNANDO("evaluaciones.aplicacion.evaluacionasesor.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("evaluaciones.aplicacion.evaluacionasesor.log.verificacion-asignar", 5),
    LOG_ASIGNADA("evaluaciones.aplicacion.evaluacionasesor.log.asignada", 2),
    LOG_SIN_CAMBIOS("evaluaciones.aplicacion.evaluacionasesor.log.sin-cambios", 2),
    LOG_GUARDADA("evaluaciones.infraestructura.evaluacionasesor.log.guardada", 1),
    LOG_ASESOR_ACTUALIZADO("evaluaciones.infraestructura.evaluacionasesor.log.asesor-actualizado", 2),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacionasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacionasesor.log.consulta-completada", 1),
    ERROR_REALIZADA_NO_DISPONIBLE(
            "evaluaciones.infraestructura.evaluacionasesor.error.realizada-no-disponible", 0),
    ERROR_REALIZADA_NO_DISPONIBLE_DETALLE(
            "evaluaciones.infraestructura.evaluacionasesor.error.realizada-no-disponible-detalle", 1),
    LOG_CONSULTANDO_REALIZADA("evaluaciones.aplicacion.evaluacionasesor.log.consultando-realizada", 1),
    LOG_CONSULTA_REALIZADA_COMPLETADA(
            "evaluaciones.aplicacion.evaluacionasesor.log.consulta-realizada-completada", 1),
    LOG_CONSULTANDO_ASIGNADAS("evaluaciones.aplicacion.evaluacionasesor.log.consultando-asignadas", 5),
    LOG_CONSULTA_ASIGNADAS_COMPLETADA(
            "evaluaciones.aplicacion.evaluacionasesor.log.consulta-asignadas-completada", 3);

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
