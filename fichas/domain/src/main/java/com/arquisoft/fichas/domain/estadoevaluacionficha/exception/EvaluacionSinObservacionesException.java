package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

import java.util.UUID;

public final class EvaluacionSinObservacionesException extends DomainException {

    public EvaluacionSinObservacionesException(UUID evaluacionFichaPerfil) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.OBSERVACIONES_REQUERIDAS,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_OBSERVACIONES_REQUERIDAS),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_OBSERVACIONES_REQUERIDAS_DETALLE,
                        evaluacionFichaPerfil)
        ));
    }
}
