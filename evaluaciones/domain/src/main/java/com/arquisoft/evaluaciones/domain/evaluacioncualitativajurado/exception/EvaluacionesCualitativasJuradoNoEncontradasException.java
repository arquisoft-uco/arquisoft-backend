package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCualitativasJuradoNoEncontradasException extends DomainException {

    public EvaluacionesCualitativasJuradoNoEncontradasException(Set<UUID> evaluaciones, UUID evaluacionJurado) {
        super(
                Mensajes.formatear(
                        EvaluacionCualitativaJuradoKey.ERROR_EVALUACIONES_NO_ENCONTRADAS, evaluaciones, evaluacionJurado),
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACIONES_NO_ENCONTRADAS
        );
    }
}
