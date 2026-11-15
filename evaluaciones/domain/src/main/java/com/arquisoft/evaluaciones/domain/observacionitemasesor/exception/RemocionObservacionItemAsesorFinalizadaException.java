package com.arquisoft.evaluaciones.domain.observacionitemasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;

import java.util.UUID;

public final class RemocionObservacionItemAsesorFinalizadaException extends DomainException {

    public RemocionObservacionItemAsesorFinalizadaException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemAsesor.REMOCION_EVALUACION_FINALIZADA,
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_REMOCION_EVALUACION_FINALIZADA),
                Mensajes.formatear(
                        ObservacionItemAsesorKey.ERROR_REMOCION_EVALUACION_FINALIZADA_DETALLE, evaluacionAsesor)
        ));
    }
}
