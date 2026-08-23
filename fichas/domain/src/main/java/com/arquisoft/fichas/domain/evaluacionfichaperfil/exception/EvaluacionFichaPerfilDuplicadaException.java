package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class EvaluacionFichaPerfilDuplicadaException extends DomainException {

    public EvaluacionFichaPerfilDuplicadaException(UUID representanteId, UUID fichaId) {
        super(
                Mensajes.formatear(EvaluacionFichaPerfilKey.ERROR_DUPLICADA, representanteId, fichaId),
                FichasCodes.EvaluacionFichaPerfil.EVALUACION_DUPLICADA
        );
    }
}
