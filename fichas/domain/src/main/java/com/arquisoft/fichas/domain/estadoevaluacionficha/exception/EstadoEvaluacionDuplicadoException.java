package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class EstadoEvaluacionDuplicadoException extends DomainException {

    public EstadoEvaluacionDuplicadoException(UUID evaluacionId, EstadoEvaluacion estadoEvaluacion) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.ESTADO_DUPLICADO,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_ESTADO_DUPLICADO, estadoEvaluacion.getNombre()),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_ESTADO_DUPLICADO_DETALLE, evaluacionId, estadoEvaluacion.getId())
        ));
    }
}
