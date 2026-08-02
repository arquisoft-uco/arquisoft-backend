package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public class EvaluacionFichaPerfilDuplicadaException extends ApplicationException {

    public EvaluacionFichaPerfilDuplicadaException(UUID representanteId, UUID fichaId) {
        super(
                Messages.obtener(FichasKeys.EvaluacionFichaPerfil.ERROR_DUPLICADA)
                        .formatted(representanteId, fichaId),
                FichasCodes.EvaluacionFichaPerfil.EVALUACION_DUPLICADA
        );
    }
}
