package com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;

public final class EstadoEvaluacionAsignadaJuradoNoEncontradoException extends DomainException {

    public EstadoEvaluacionAsignadaJuradoNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionJurado.ESTADO_NO_ENCONTRADO,
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_ESTADO_NO_ENCONTRADO),
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_ESTADO_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
