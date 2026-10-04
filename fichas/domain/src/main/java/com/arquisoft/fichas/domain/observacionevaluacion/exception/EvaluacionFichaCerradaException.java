package com.arquisoft.fichas.domain.observacionevaluacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;

import java.util.UUID;

public final class EvaluacionFichaCerradaException extends DomainException {

    public EvaluacionFichaCerradaException(UUID evaluacionFichaPerfil, String estadoEvaluacion) {
        super(
                Mensajes.formatear(ObservacionEvaluacionKey.ERROR_EVALUACION_CERRADA,
                        evaluacionFichaPerfil, estadoEvaluacion),
                FichasCodes.ObservacionEvaluacion.EVALUACION_CERRADA
        );
    }
}
