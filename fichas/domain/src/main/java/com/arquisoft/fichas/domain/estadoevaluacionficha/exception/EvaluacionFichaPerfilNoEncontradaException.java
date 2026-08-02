package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public class EvaluacionFichaPerfilNoEncontradaException extends ApplicationException {

    public EvaluacionFichaPerfilNoEncontradaException(UUID evaluacionId) {
        super(
                FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA_MSG.formatted(evaluacionId),
                FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA);
    }
}
