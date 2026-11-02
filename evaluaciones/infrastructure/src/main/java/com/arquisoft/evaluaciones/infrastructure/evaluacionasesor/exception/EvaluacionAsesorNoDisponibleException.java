package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;

import java.util.UUID;

public final class EvaluacionAsesorNoDisponibleException extends InfrastructureException {

    public EvaluacionAsesorNoDisponibleException(UUID evaluacion) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionAsesor.NO_DISPONIBLE,
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_NO_DISPONIBLE),
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_NO_DISPONIBLE_DETALLE, evaluacion)
        ));
    }
}
