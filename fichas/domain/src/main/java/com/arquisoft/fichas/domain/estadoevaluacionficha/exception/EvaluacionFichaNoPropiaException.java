package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.AuthorizationException;

import java.util.UUID;

public final class EvaluacionFichaNoPropiaException extends AuthorizationException {

    public EvaluacionFichaNoPropiaException(UUID evaluacionFichaPerfilId) {
        super(
                Messages.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_PROPIA, evaluacionFichaPerfilId),
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_PROPIA
        );
    }
}
