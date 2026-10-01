package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

public final class EstadoEvaluacionTerminalException extends DomainException {

    public EstadoEvaluacionTerminalException() {
        super(
                Mensajes.obtener(EstadoEvaluacionFichaKey.ERROR_TRANSICION_DESDE_TERMINAL),
                FichasCodes.EstadoEvaluacionFicha.TRANSICION_INVALIDA
        );
    }
}
