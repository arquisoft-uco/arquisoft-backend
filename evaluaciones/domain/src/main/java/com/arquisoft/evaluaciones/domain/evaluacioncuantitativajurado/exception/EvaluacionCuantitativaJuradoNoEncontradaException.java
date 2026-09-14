package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionCuantitativaJuradoNoEncontradaException extends DomainException {

    public EvaluacionCuantitativaJuradoNoEncontradaException(UUID evaluacionCuantitativaJurado) {
        super(
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_NO_ENCONTRADA, evaluacionCuantitativaJurado),
                EvaluacionesCodes.EvaluacionCuantitativaJurado.NO_ENCONTRADA
        );
    }
}
