package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class EvaluacionFichaPerfilNoEncontradaException extends DomainException {

    public EvaluacionFichaPerfilNoEncontradaException(UUID evaluacionId) {
        super(
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_ENCONTRADA, evaluacionId),
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA);
    }
}
