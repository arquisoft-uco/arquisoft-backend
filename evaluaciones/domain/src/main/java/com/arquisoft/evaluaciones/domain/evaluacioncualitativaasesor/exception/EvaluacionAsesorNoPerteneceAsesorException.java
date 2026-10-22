package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.UUID;

public final class EvaluacionAsesorNoPerteneceAsesorException extends DomainException {

    public EvaluacionAsesorNoPerteneceAsesorException(UUID evaluacionAsesor) {
        super(
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_NO_PERTENECE_ASESOR, evaluacionAsesor),
                EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR
        );
    }
}
