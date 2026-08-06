package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de EstadoEvaluacionFicha. */
public enum EstadoEvaluacionFichaKey implements MessageKey {

    ERROR_EVALUACION_NO_ENCONTRADA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-encontrada"),
    ERROR_EVALUACION_NO_PROPIA("fichas.dominio.estadoevaluacionficha.error.evaluacion-no-propia"),
    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.estadoevaluacionficha.error.estado-no-encontrado"),
    ERROR_ESTADO_DUPLICADO("fichas.dominio.estadoevaluacionficha.error.estado-duplicado"),
    ERROR_TRANSICION_DESDE_TERMINAL("fichas.dominio.estadoevaluacionficha.error.transicion-desde-terminal"),
    ERROR_EN_EVALUACION_NO_MANUAL("fichas.dominio.estadoevaluacionficha.error.en-evaluacion-no-manual"),
    LOG_AGREGADO("fichas.aplicacion.estadoevaluacionficha.log.agregado"),
    LOG_CREADO_AUTOMATICO("fichas.aplicacion.estadoevaluacionficha.log.creado-automatico");

    private final String clave;

    EstadoEvaluacionFichaKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.FICHAS;
    }
}
