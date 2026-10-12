package com.arquisoft.fichas.domain.observacionevaluacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;

import java.util.UUID;

public final class ObservacionEvaluacionDuplicadaException extends DomainException {

    public ObservacionEvaluacionDuplicadaException(UUID evaluacionFichaPerfil, String observacion) {
        super(
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_OBSERVACION_EVALUACION_DUPLICADA,
                        evaluacionFichaPerfil, observacion),
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_DUPLICADA
        );
    }
}
