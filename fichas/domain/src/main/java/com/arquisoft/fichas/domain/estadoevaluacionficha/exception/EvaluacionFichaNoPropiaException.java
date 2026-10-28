package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public final class EvaluacionFichaNoPropiaException extends DomainException {

    public EvaluacionFichaNoPropiaException(UUID evaluacionFichaPerfilId) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_PROPIA,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_PROPIA),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_PROPIA_DETALLE, evaluacionFichaPerfilId)
        ));
    }
}
