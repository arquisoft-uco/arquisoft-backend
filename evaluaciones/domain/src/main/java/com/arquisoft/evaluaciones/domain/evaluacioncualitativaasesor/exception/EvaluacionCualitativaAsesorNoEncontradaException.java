package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.UUID;

public final class EvaluacionCualitativaAsesorNoEncontradaException extends DomainException {

    public EvaluacionCualitativaAsesorNoEncontradaException(UUID evaluacionCualitativaAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_NO_ENCONTRADA,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_EVALUACION_CUALITATIVA_NO_ENCONTRADA),
                Mensajes.formatear(
                        EvaluacionCualitativaAsesorKey.ERROR_EVALUACION_CUALITATIVA_NO_ENCONTRADA_DETALLE,
                        evaluacionCualitativaAsesor)
        ));
    }
}
