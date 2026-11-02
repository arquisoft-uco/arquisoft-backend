package com.arquisoft.evaluaciones.domain.evaluacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;

import java.util.UUID;

public final class EvaluacionSinEvaluacionAsesorException extends DomainException {

    public EvaluacionSinEvaluacionAsesorException(UUID evaluacion) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionAsesor.SIN_EVALUACION_ASESOR,
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_SIN_EVALUACION_ASESOR),
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_SIN_EVALUACION_ASESOR_DETALLE, evaluacion)
        ));
    }
}
