package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

import java.util.UUID;

public final class EvaluacionNoDescartableException extends DomainException {

    public EvaluacionNoDescartableException(UUID evaluacionFichaPerfilId, EstadoEvaluacion estadoActual) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_DESCARTABLE,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_DESCARTABLE, estadoActual.getNombre()),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_EVALUACION_NO_DESCARTABLE_DETALLE,
                        evaluacionFichaPerfilId, estadoActual.getId())
        ));
    }
}
