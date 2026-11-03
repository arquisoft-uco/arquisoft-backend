package com.arquisoft.evaluaciones.domain.evaluacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;

import java.util.UUID;

public final class EvaluacionAsesorNoPerteneceEstudianteException extends DomainException {

    public EvaluacionAsesorNoPerteneceEstudianteException(UUID evaluacionAsesor, UUID estudiante) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionAsesor.NO_PERTENECE_ESTUDIANTE,
                Mensajes.formatear(EvaluacionAsesorKey.ERROR_NO_PERTENECE_ESTUDIANTE),
                Mensajes.formatear(
                        EvaluacionAsesorKey.ERROR_NO_PERTENECE_ESTUDIANTE_DETALLE, evaluacionAsesor, estudiante)
        ));
    }
}
