package com.arquisoft.evaluaciones.domain.estadoevaluacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

public final class EstadoEvaluacionNoEncontradoException extends DomainException {

    public EstadoEvaluacionNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EvaluacionKey.ERROR_ESTADO_NO_ENCONTRADO, id),
                EvaluacionesCodes.Evaluacion.ESTADO_NO_ENCONTRADO
        );
    }
}
