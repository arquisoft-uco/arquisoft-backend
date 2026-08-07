package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public class EvaluacionFichaPerfilDuplicadaException extends ApplicationException {

    public EvaluacionFichaPerfilDuplicadaException(UUID representanteId, UUID fichaId) {
        super(
                Mensajes.obtener(EvaluacionFichaPerfilKey.ERROR_DUPLICADA)
                        .formatted(representanteId, fichaId),
                FichasCodes.EvaluacionFichaPerfil.EVALUACION_DUPLICADA
        );
    }
}
