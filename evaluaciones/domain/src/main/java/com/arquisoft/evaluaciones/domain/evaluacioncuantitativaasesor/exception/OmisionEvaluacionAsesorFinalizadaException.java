package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.UUID;

public final class OmisionEvaluacionAsesorFinalizadaException extends DomainException {

    public OmisionEvaluacionAsesorFinalizadaException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.OMISION_EVALUACION_ASESOR_FINALIZADA,
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_OMISION_EVALUACION_ASESOR_FINALIZADA),
                Mensajes.formatear(
                        EvaluacionCuantitativaAsesorKey.ERROR_OMISION_EVALUACION_ASESOR_FINALIZADA_DETALLE,
                        evaluacionAsesor)
        ));
    }
}
