package com.arquisoft.evaluaciones.domain.observacionitemjurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;

import java.util.UUID;

public final class EvaluacionCuantitativaJuradoNoEncontradaException extends DomainException {

    public EvaluacionCuantitativaJuradoNoEncontradaException(UUID evaluacionCuantitativaJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_NO_ENCONTRADA,
                Mensajes.formatear(ObservacionItemJuradoKey.ERROR_EVALUACION_CUANTITATIVA_JURADO_NO_ENCONTRADA),
                Mensajes.formatear(ObservacionItemJuradoKey.ERROR_EVALUACION_CUANTITATIVA_JURADO_NO_ENCONTRADA_DETALLE, evaluacionCuantitativaJurado)
        ));
    }
}
