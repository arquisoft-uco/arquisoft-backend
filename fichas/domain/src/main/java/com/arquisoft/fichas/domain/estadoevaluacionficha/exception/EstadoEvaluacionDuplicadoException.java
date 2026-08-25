package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class EstadoEvaluacionDuplicadoException extends DomainException {

    public EstadoEvaluacionDuplicadoException(UUID evaluacionId, String estadoId) {
        super(
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_ESTADO_DUPLICADO, evaluacionId, estadoId),
                FichasCodes.EstadoEvaluacionFicha.ESTADO_DUPLICADO);
    }
}
