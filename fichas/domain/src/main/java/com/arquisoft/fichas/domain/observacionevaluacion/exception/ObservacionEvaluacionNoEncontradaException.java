package com.arquisoft.fichas.domain.observacionevaluacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;

import java.util.UUID;

public final class ObservacionEvaluacionNoEncontradaException extends DomainException {

    public ObservacionEvaluacionNoEncontradaException(UUID observacionEvaluacion) {
        super(BaseError.conDetalle(
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_NO_ENCONTRADA,
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_OBSERVACION_EVALUACION_NO_ENCONTRADA),
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_OBSERVACION_EVALUACION_NO_ENCONTRADA_DETALLE, observacionEvaluacion)
        ));
    }
}
