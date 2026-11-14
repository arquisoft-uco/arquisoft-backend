package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.UUID;

public final class CambioCriterioEvaluacionCualitativaAsesorFinalizadaException extends DomainException {

    public CambioCriterioEvaluacionCualitativaAsesorFinalizadaException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CAMBIO_CRITERIO_EVALUACION_ASESOR_FINALIZADA,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_CAMBIO_CRITERIO_FINALIZADA),
                Mensajes.formatear(
                        EvaluacionCualitativaAsesorKey.ERROR_CAMBIO_CRITERIO_FINALIZADA_DETALLE,
                        evaluacionAsesor)
        ));
    }
}
