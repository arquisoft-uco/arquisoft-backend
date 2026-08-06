package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public class EvaluacionFichaPerfilNoEncontradaException extends ApplicationException {

    public EvaluacionFichaPerfilNoEncontradaException(UUID evaluacionId) {
        super(
                Messages.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_ENCONTRADA, evaluacionId),
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA);
    }
}
