package com.arquisoft.evaluaciones.domain.observacionitemjurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;

import java.util.UUID;

public final class EvaluacionCuantitativaJuradoNoPerteneceJuradoException extends DomainException {

    public EvaluacionCuantitativaJuradoNoPerteneceJuradoException(UUID evaluacionCuantitativaJurado) {
        super(
                Mensajes.formatear(
                        ObservacionItemJuradoKey.ERROR_EVALUACION_CUANTITATIVA_JURADO_NO_PERTENECE_JURADO,
                        evaluacionCuantitativaJurado),
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_NO_PERTENECE_JURADO
        );
    }
}
