package com.arquisoft.evaluaciones.domain.evaluacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;

import java.util.UUID;

public final class EvaluacionAsesorConEvaluacionesException extends DomainException {

    public EvaluacionAsesorConEvaluacionesException(UUID evaluacion, UUID asesorActual) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionAsesor.CON_EVALUACIONES,
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_CON_EVALUACIONES),
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_CON_EVALUACIONES_DETALLE, evaluacion, asesorActual)
        ));
    }
}
