package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.UUID;

public final class CambioPuntajeEvaluacionAsesorFinalizadaException extends DomainException {

    public CambioPuntajeEvaluacionAsesorFinalizadaException(UUID evaluacionAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.CAMBIO_PUNTAJE_EVALUACION_FINALIZADA,
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_CAMBIO_PUNTAJE_FINALIZADA),
                Mensajes.formatear(
                        EvaluacionCuantitativaAsesorKey.ERROR_CAMBIO_PUNTAJE_FINALIZADA_DETALLE, evaluacionAsesor)
        ));
    }
}
