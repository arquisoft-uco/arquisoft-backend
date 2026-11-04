package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionJuradoFinalizadaException extends DomainException {

    public EvaluacionJuradoFinalizadaException(UUID evaluacionJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_FINALIZADA,
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_EVALUACION_JURADO_FINALIZADA),
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_EVALUACION_JURADO_FINALIZADA_DETALLE, evaluacionJurado)
        ));
    }
}
