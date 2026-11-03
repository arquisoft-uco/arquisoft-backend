package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionCualitativaJuradoNoEncontradaException extends DomainException {

    public EvaluacionCualitativaJuradoNoEncontradaException(UUID evaluacionCualitativaJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaJurado.NO_ENCONTRADA,
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_NO_ENCONTRADA_DETALLE, evaluacionCualitativaJurado)
        ));
    }
}
