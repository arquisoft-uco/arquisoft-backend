package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCuantitativasAsesorNoRegistradasException extends DomainException {

    public EvaluacionesCuantitativasAsesorNoRegistradasException(UUID evaluacionAsesor, Set<UUID> itemsFaltantes) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACIONES_NO_REGISTRADAS,
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_EVALUACIONES_NO_REGISTRADAS),
                Mensajes.formatear(
                        EvaluacionCuantitativaAsesorKey.ERROR_EVALUACIONES_NO_REGISTRADAS_DETALLE,
                        evaluacionAsesor, itemsFaltantes)
        ));
    }
}
