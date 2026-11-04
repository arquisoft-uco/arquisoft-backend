package com.arquisoft.evaluaciones.domain.evaluacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

import java.util.UUID;

public final class EvaluacionNoEncontradaException extends DomainException {

    public EvaluacionNoEncontradaException(UUID evaluacion) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.Evaluacion.NO_ENCONTRADA,
                Mensajes.formatear(EvaluacionKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(EvaluacionKey.ERROR_NO_ENCONTRADA_DETALLE, evaluacion)
        ));
    }
}
