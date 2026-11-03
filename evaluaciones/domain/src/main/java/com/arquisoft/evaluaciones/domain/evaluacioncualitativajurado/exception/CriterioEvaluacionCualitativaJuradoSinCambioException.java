package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.UUID;

public final class CriterioEvaluacionCualitativaJuradoSinCambioException extends DomainException {

    public CriterioEvaluacionCualitativaJuradoSinCambioException(UUID evaluacionCualitativaJurado, UUID criterio) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_SIN_CAMBIO,
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_CRITERIO_SIN_CAMBIO),
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_CRITERIO_SIN_CAMBIO_DETALLE, evaluacionCualitativaJurado, criterio)
        ));
    }
}
