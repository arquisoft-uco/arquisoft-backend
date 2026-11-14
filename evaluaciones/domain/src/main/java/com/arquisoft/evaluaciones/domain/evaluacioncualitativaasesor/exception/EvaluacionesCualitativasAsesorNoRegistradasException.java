package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCualitativasAsesorNoRegistradasException extends DomainException {

    public EvaluacionesCualitativasAsesorNoRegistradasException(UUID evaluacionAsesor, Set<UUID> itemsFaltantes) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACIONES_NO_REGISTRADAS,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_EVALUACIONES_NO_REGISTRADAS),
                Mensajes.formatear(
                        EvaluacionCualitativaAsesorKey.ERROR_EVALUACIONES_NO_REGISTRADAS_DETALLE,
                        evaluacionAsesor, itemsFaltantes)
        ));
    }
}
