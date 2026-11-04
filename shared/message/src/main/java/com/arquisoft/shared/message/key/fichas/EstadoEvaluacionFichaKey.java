package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoEvaluacionFicha. */
public enum EstadoEvaluacionFichaKey implements ClaveMensaje {

    ERROR_EVALUACION_NO_ENCONTRADA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-encontrada", 0),
    ERROR_EVALUACION_NO_ENCONTRADA_DETALLE(
            "fichas.dominio.estadoevaluacionficha.error.evaluacion-no-encontrada-detalle", 1),
    ERROR_EVALUACION_NO_PROPIA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-propia", 0),
    ERROR_EVALUACION_NO_PROPIA_DETALLE("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-propia-detalle", 1),
    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.estadoevaluacionficha.error.estado-no-encontrado", 0),
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("fichas.dominio.estadoevaluacionficha.error.estado-no-encontrado-detalle", 1),
    ERROR_ESTADO_DUPLICADO("fichas.dominio.estadoevaluacionficha.error.estado-duplicado", 1),
    ERROR_ESTADO_DUPLICADO_DETALLE("fichas.dominio.estadoevaluacionficha.error.estado-duplicado-detalle", 2),
    ERROR_TRANSICION_DESDE_TERMINAL("fichas.dominio.estadoevaluacionficha.error.transicion-desde-terminal", 0),
    ERROR_EN_EVALUACION_NO_MANUAL("fichas.dominio.estadoevaluacionficha.error.en-evaluacion-no-manual", 0),
    ERROR_EN_EVALUACION_NO_MANUAL_DETALLE(
            "fichas.dominio.estadoevaluacionficha.error.en-evaluacion-no-manual-detalle", 0),
    LOG_AGREGANDO("fichas.aplicacion.estadoevaluacionficha.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.estadoevaluacionficha.log.verificacion-agregar", 3),
    LOG_AGREGADO("fichas.aplicacion.estadoevaluacionficha.log.agregado", 3),
    LOG_CREADO_AUTOMATICO("fichas.aplicacion.estadoevaluacionficha.log.creado-automatico", 3),
    LOG_GUARDADO("fichas.infraestructura.estadoevaluacionficha.log.guardado", 2);

    private final String clave;
    private final int parametros;

    EstadoEvaluacionFichaKey(String clave, int parametros) {
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
