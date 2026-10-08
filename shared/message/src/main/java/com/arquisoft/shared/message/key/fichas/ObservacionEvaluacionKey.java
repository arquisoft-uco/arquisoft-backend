package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionEvaluacionKey implements ClaveMensaje {

    ERROR_OBSERVACION_EVALUACION_DUPLICADA("fichas.dominio.observacionevaluacion.error.duplicada", 2),
    ERROR_EVALUACION_CERRADA("fichas.dominio.observacionevaluacion.error.evaluacion-cerrada", 2),
    ERROR_OBSERVACION_EVALUACION_NO_ENCONTRADA("fichas.dominio.observacionevaluacion.error.no-encontrada", 1),
    LOG_AGREGANDO("fichas.aplicacion.observacionevaluacion.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.observacionevaluacion.log.verificacion-agregar", 4),
    LOG_AGREGADA("fichas.aplicacion.observacionevaluacion.log.agregada", 2),
    LOG_MODIFICANDO("fichas.aplicacion.observacionevaluacion.log.modificando", 2),
    LOG_VERIFICACION_MODIFICAR("fichas.aplicacion.observacionevaluacion.log.verificacion-modificar", 4),
    LOG_MODIFICADA("fichas.aplicacion.observacionevaluacion.log.modificada", 2),
    LOG_REMOVIENDO("fichas.aplicacion.observacionevaluacion.log.removiendo", 2),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.observacionevaluacion.log.verificacion-remover", 3),
    LOG_REMOVIDA("fichas.aplicacion.observacionevaluacion.log.removida", 2),
    LOG_CONSULTANDO_ESTUDIANTE("fichas.aplicacion.observacionevaluacion.log.consultando-estudiante", 2),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("fichas.aplicacion.observacionevaluacion.log.consulta-estudiante-completada", 1),
    LOG_CONSULTANDO_ASESOR("fichas.aplicacion.observacionevaluacion.log.consultando-asesor", 2),
    LOG_CONSULTA_ASESOR_COMPLETADA("fichas.aplicacion.observacionevaluacion.log.consulta-asesor-completada", 1),
    LOG_CONSULTANDO_REPRESENTANTE("fichas.aplicacion.observacionevaluacion.log.consultando-representante", 2),
    LOG_CONSULTA_REPRESENTANTE_COMPLETADA("fichas.aplicacion.observacionevaluacion.log.consulta-representante-completada", 1),
    LOG_GUARDADA("fichas.infraestructura.observacionevaluacion.log.guardada", 1),
    LOG_ACTUALIZADA("fichas.infraestructura.observacionevaluacion.log.actualizada", 1),
    LOG_ELIMINADA("fichas.infraestructura.observacionevaluacion.log.eliminada", 1);

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
