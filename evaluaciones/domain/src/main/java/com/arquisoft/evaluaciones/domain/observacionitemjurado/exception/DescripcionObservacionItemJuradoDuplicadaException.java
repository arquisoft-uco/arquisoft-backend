package com.arquisoft.evaluaciones.domain.observacionitemjurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;

import java.util.UUID;

public final class DescripcionObservacionItemJuradoDuplicadaException extends DomainException {

    public DescripcionObservacionItemJuradoDuplicadaException(UUID evaluacionCuantitativaJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DUPLICADA,
                Mensajes.formatear(ObservacionItemJuradoKey.ERROR_DESCRIPCION_DUPLICADA),
                Mensajes.formatear(ObservacionItemJuradoKey.ERROR_DESCRIPCION_DUPLICADA_DETALLE, evaluacionCuantitativaJurado)
        ));
    }
}
