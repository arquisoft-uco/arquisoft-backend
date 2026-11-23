package com.arquisoft.evaluaciones.domain.evaluacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

import java.util.UUID;

public final class ResumenesEvaluacionIncompletosException extends DomainException {

    public ResumenesEvaluacionIncompletosException(UUID evaluacion, EvaluacionKey mensaje) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.Evaluacion.RESUMENES_INCOMPLETOS,
                Mensajes.formatear(mensaje),
                Mensajes.formatear(EvaluacionKey.ERROR_RESUMENES_INCOMPLETOS_DETALLE, evaluacion)
        ));
    }
}
