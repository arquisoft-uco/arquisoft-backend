package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionJuradoNoPerteneceJuradoException extends DomainException {

    public EvaluacionJuradoNoPerteneceJuradoException(UUID evaluacionJurado) {
        super(
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_NO_PERTENECE_JURADO, evaluacionJurado),
                EvaluacionesCodes.EvaluacionCualitativaJurado.NO_PERTENECE_JURADO
        );
    }
}
