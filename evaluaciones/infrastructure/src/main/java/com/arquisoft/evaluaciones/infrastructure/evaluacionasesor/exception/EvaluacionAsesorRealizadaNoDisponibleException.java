package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;

import java.util.UUID;

public final class EvaluacionAsesorRealizadaNoDisponibleException extends InfrastructureException {

    public EvaluacionAsesorRealizadaNoDisponibleException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionAsesor.REALIZADA_NO_DISPONIBLE,
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_REALIZADA_NO_DISPONIBLE),
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_REALIZADA_NO_DISPONIBLE_DETALLE, evaluacionAsesor)
        ));
    }
}
