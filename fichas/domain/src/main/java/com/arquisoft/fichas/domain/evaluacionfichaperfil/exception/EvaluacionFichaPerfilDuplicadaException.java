package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public class EvaluacionFichaPerfilDuplicadaException extends ApplicationException {

    public EvaluacionFichaPerfilDuplicadaException(UUID representanteId, UUID fichaId) {
        super(
                FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA_MSG
                        .formatted(representanteId, fichaId),
                FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA
        );
    }
}
