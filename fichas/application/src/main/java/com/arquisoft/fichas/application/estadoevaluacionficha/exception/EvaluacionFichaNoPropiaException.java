package com.arquisoft.fichas.application.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.AuthorizationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class EvaluacionFichaNoPropiaException extends AuthorizationException {

    public EvaluacionFichaNoPropiaException(UUID evaluacionFichaPerfilId) {
        super(
                FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_PROPIA_MSG.formatted(evaluacionFichaPerfilId),
                FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_PROPIA
        );
    }
}
