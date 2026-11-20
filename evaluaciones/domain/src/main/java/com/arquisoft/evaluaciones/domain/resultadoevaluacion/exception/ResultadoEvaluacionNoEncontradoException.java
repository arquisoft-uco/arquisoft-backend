package com.arquisoft.evaluaciones.domain.resultadoevaluacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

public final class ResultadoEvaluacionNoEncontradoException extends DomainException {

    public ResultadoEvaluacionNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ResultadoEvaluacion.NO_ENCONTRADO,
                Mensajes.formatear(EvaluacionKey.ERROR_RESULTADO_NO_ENCONTRADO),
                Mensajes.formatear(EvaluacionKey.ERROR_RESULTADO_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
