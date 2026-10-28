package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCuantitativasJuradoConObservacionesException extends DomainException {

    public EvaluacionesCuantitativasJuradoConObservacionesException(Set<UUID> evaluaciones, UUID evaluacionJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACIONES_CON_OBSERVACIONES,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_EVALUACIONES_CON_OBSERVACIONES),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_EVALUACIONES_CON_OBSERVACIONES_DETALLE, evaluaciones, evaluacionJurado)
        ));
    }
}
