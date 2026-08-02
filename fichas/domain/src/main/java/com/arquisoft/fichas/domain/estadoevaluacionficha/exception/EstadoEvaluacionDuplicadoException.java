package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public class EstadoEvaluacionDuplicadoException extends ApplicationException {

    public EstadoEvaluacionDuplicadoException(UUID evaluacionId, String estadoId) {
        super(
                FichasMessages.EstadoEvaluacionFicha.ESTADO_DUPLICADO_MSG.formatted(evaluacionId, estadoId),
                FichasMessages.EstadoEvaluacionFicha.ESTADO_DUPLICADO);
    }
}
