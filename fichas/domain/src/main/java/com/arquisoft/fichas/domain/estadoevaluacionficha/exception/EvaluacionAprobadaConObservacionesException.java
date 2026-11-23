package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

import java.util.UUID;

public final class EvaluacionAprobadaConObservacionesException extends DomainException {

    public EvaluacionAprobadaConObservacionesException(UUID evaluacionFichaPerfil, long observaciones) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.OBSERVACIONES_NO_PERMITIDAS,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_APROBADA_CON_OBSERVACIONES),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_APROBADA_CON_OBSERVACIONES_DETALLE,
                        evaluacionFichaPerfil, observaciones)
        ));
    }
}
