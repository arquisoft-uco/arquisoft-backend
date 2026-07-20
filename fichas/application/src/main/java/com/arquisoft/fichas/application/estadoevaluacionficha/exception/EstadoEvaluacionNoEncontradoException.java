package com.arquisoft.fichas.application.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

public class EstadoEvaluacionNoEncontradoException extends ApplicationException {

    public EstadoEvaluacionNoEncontradoException(String estadoId) {
        super(
                FichasMessages.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO_MSG.formatted(estadoId),
                FichasMessages.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO);
    }
}
