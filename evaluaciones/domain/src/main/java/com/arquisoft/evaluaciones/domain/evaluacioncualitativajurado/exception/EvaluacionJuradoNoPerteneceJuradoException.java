package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

public final class EvaluacionJuradoNoPerteneceJuradoException extends DomainException {

    public EvaluacionJuradoNoPerteneceJuradoException() {
        super(
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_EVALUACION_JURADO_NO_PERTENECE_JURADO),
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_PERTENECE_JURADO
        );
    }
}
