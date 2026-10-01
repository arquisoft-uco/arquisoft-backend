package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

public final class EstadoEnEvaluacionNoManualException extends DomainException {

    public EstadoEnEvaluacionNoManualException() {
        super(
                Mensajes.obtener(EstadoEvaluacionFichaKey.ERROR_EN_EVALUACION_NO_MANUAL),
                FichasCodes.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL
        );
    }
}
