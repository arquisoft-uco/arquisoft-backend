package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.UUID;

public final class OmisionEvaluacionCualitativaAsesorFinalizadaException extends DomainException {

    public OmisionEvaluacionCualitativaAsesorFinalizadaException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.OMISION_EVALUACION_ASESOR_FINALIZADA,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_OMISION_FINALIZADA),
                Mensajes.formatear(
                        EvaluacionCualitativaAsesorKey.ERROR_OMISION_FINALIZADA_DETALLE,
                        evaluacionAsesor)
        ));
    }
}
