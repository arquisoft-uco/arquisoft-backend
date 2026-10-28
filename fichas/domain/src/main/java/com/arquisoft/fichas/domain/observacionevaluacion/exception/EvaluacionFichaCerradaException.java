package com.arquisoft.fichas.domain.observacionevaluacion.exception;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;

import java.util.UUID;

public final class EvaluacionFichaCerradaException extends DomainException {

    public EvaluacionFichaCerradaException(UUID evaluacionFichaPerfil, EstadoEvaluacion estadoEvaluacion) {
        super(BaseError.conDetalle(
                FichasCodes.ObservacionEvaluacion.EVALUACION_CERRADA,
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_EVALUACION_CERRADA, estadoEvaluacion.getNombre()),
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_EVALUACION_CERRADA_DETALLE, evaluacionFichaPerfil, estadoEvaluacion.getId())
        ));
    }
}
