package com.arquisoft.evaluaciones.domain.observacionitemasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;

import java.util.UUID;

public final class DescripcionObservacionItemAsesorDuplicadaException extends DomainException {

    public DescripcionObservacionItemAsesorDuplicadaException(UUID evaluacionCuantitativaAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DUPLICADA,
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_DESCRIPCION_DUPLICADA),
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_DESCRIPCION_DUPLICADA_DETALLE,
                        evaluacionCuantitativaAsesor)
        ));
    }
}
