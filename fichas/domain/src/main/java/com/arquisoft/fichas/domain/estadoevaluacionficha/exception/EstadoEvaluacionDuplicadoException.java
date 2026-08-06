package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public class EstadoEvaluacionDuplicadoException extends ApplicationException {

    public EstadoEvaluacionDuplicadoException(UUID evaluacionId, String estadoId) {
        super(
                Messages.formatear(EstadoEvaluacionFichaKey.ERROR_ESTADO_DUPLICADO, evaluacionId, estadoId),
                FichasCodes.EstadoEvaluacionFicha.ESTADO_DUPLICADO);
    }
}
