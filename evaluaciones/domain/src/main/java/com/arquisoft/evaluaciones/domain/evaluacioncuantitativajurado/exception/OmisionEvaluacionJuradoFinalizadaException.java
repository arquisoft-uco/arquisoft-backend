package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.UUID;

public final class OmisionEvaluacionJuradoFinalizadaException extends DomainException {

    public OmisionEvaluacionJuradoFinalizadaException(UUID evaluacionJurado) {
        super(
                Mensajes.formatear(
                        EvaluacionCuantitativaJuradoKey.ERROR_OMISION_EVALUACION_JURADO_FINALIZADA, evaluacionJurado),
                EvaluacionesCodes.EvaluacionCuantitativaJurado.OMISION_EVALUACION_JURADO_FINALIZADA
        );
    }
}
