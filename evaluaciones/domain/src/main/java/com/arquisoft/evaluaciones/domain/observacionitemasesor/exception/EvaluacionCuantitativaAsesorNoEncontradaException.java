package com.arquisoft.evaluaciones.domain.observacionitemasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;

import java.util.UUID;

public final class EvaluacionCuantitativaAsesorNoEncontradaException extends DomainException {

    public EvaluacionCuantitativaAsesorNoEncontradaException(UUID evaluacionCuantitativaAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_NO_ENCONTRADA,
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_EVALUACION_NO_ENCONTRADA),
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_EVALUACION_NO_ENCONTRADA_DETALLE,
                        evaluacionCuantitativaAsesor)
        ));
    }
}
