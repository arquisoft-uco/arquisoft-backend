package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionJuradoFinalizadaException extends DomainException {

    public EvaluacionJuradoFinalizadaException(UUID evaluacionCuantitativaJurado) {
        super(
                Mensajes.formatear(
                        EvaluacionCuantitativaJuradoKey.ERROR_EVALUACION_FINALIZADA, evaluacionCuantitativaJurado),
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_FINALIZADA
        );
    }
}
