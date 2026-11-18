package com.arquisoft.evaluaciones.domain.evaluacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

public final class EvaluacionFinalizadaAsignacionException extends DomainException {

    public EvaluacionFinalizadaAsignacionException() {
        super(
                Mensajes.formatear(EvaluacionKey.ERROR_ASIGNACION_FINALIZADA),
                EvaluacionesCodes.Evaluacion.ASIGNACION_FINALIZADA
        );
    }
}
