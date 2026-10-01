package com.arquisoft.fichas.domain.estadoevaluacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

public final class EstadoEvaluacionNoEncontradoException extends DomainException {

    public EstadoEvaluacionNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_ESTADO_NO_ENCONTRADO, id),
                FichasCodes.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO
        );
    }
}
