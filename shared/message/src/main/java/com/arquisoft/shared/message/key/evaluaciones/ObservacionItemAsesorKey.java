package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionItemAsesorKey implements ClaveMensaje {

    LOG_REGISTRANDO("evaluaciones.aplicacion.observacionitemasesor.log.registrando", 1),
    LOG_VERIFICACION_REGISTRAR("evaluaciones.aplicacion.observacionitemasesor.log.verificacion-registrar", 2),
    LOG_REGISTRADO("evaluaciones.aplicacion.observacionitemasesor.log.registrado", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.observacionitemasesor.log.guardada", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.observacionitemasesor.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.observacionitemasesor.log.consulta-completada", 3),
    ERROR_EVALUACION_NO_ENCONTRADA(
            "evaluaciones.dominio.observacionitemasesor.error.evaluacion-no-encontrada", 0),
    ERROR_EVALUACION_NO_ENCONTRADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.evaluacion-no-encontrada-detalle", 1),
    ERROR_DESCRIPCION_DUPLICADA(
            "evaluaciones.dominio.observacionitemasesor.error.descripcion-duplicada", 0),
    ERROR_DESCRIPCION_DUPLICADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.descripcion-duplicada-detalle", 1),
    LOG_MODIFICANDO("evaluaciones.aplicacion.observacionitemasesor.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR("evaluaciones.aplicacion.observacionitemasesor.log.verificacion-modificar", 3),
    LOG_MODIFICADA("evaluaciones.aplicacion.observacionitemasesor.log.modificada", 1),
    ERROR_OBSERVACION_NO_ENCONTRADA(
            "evaluaciones.dominio.observacionitemasesor.error.observacion-no-encontrada", 0),
    ERROR_OBSERVACION_NO_ENCONTRADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.observacion-no-encontrada-detalle", 1),
    ERROR_MODIFICACION_EVALUACION_FINALIZADA(
            "evaluaciones.dominio.observacionitemasesor.error.modificacion-evaluacion-finalizada", 0),
    ERROR_MODIFICACION_EVALUACION_FINALIZADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.modificacion-evaluacion-finalizada-detalle", 1),
    LOG_REMOVIENDO("evaluaciones.aplicacion.observacionitemasesor.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("evaluaciones.aplicacion.observacionitemasesor.log.verificacion-remover", 2),
    LOG_REMOVIDA("evaluaciones.aplicacion.observacionitemasesor.log.removida", 1),
    LOG_ELIMINADA("evaluaciones.infraestructura.observacionitemasesor.log.eliminada", 1),
    ERROR_REMOCION_EVALUACION_FINALIZADA(
            "evaluaciones.dominio.observacionitemasesor.error.remocion-evaluacion-finalizada", 0),
    ERROR_REMOCION_EVALUACION_FINALIZADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.remocion-evaluacion-finalizada-detalle", 1);

    private final String clave;
    private final int parametros;

    ObservacionItemAsesorKey(String clave, int parametros) {
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
