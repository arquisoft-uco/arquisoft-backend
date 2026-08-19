package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoEvaluacionFicha. */
public enum EstadoEvaluacionFichaKey implements ClaveMensaje {

    ERROR_EVALUACION_NO_ENCONTRADA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-encontrada", 1),
    ERROR_EVALUACION_NO_PROPIA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-propia", 1),
    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.estadoevaluacionficha.error.estado-no-encontrado", 1),
    ERROR_ESTADO_DUPLICADO("fichas.dominio.estadoevaluacionficha.error.estado-duplicado", 2),
    ERROR_TRANSICION_DESDE_TERMINAL("fichas.dominio.estadoevaluacionficha.error.transicion-desde-terminal", 0),
    ERROR_EN_EVALUACION_NO_MANUAL("fichas.dominio.estadoevaluacionficha.error.en-evaluacion-no-manual", 0),
    LOG_AGREGADO("fichas.aplicacion.estadoevaluacionficha.log.agregado", 0),
    LOG_CREADO_AUTOMATICO("fichas.aplicacion.estadoevaluacionficha.log.creado-automatico", 0);

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
