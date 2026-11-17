package com.arquisoft.evaluaciones.domain.observacionitemasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;

import java.util.UUID;

public final class ObservacionItemAsesorNoEncontradaException extends DomainException {

    public ObservacionItemAsesorNoEncontradaException(UUID observacion) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemAsesor.NO_ENCONTRADA,
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_OBSERVACION_NO_ENCONTRADA),
                Mensajes.formatear(ObservacionItemAsesorKey.ERROR_OBSERVACION_NO_ENCONTRADA_DETALLE, observacion)
        ));
    }
}
