package com.arquisoft.fichas.application.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

public class EstadoEvaluacionNoEncontradoException extends ApplicationException {

    public EstadoEvaluacionNoEncontradoException(String estadoId) {
        super(
                Messages.formatear(FichasKeys.EstadoEvaluacionFicha.ERROR_ESTADO_NO_ENCONTRADO, estadoId),
                FichasCodes.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO);
    }
}
