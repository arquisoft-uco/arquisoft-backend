package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCualitativasJuradoDuplicadasException extends DomainException {

    public EvaluacionesCualitativasJuradoDuplicadasException(Set<UUID> items) {
        super(
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_ITEMS_YA_REGISTRADOS, items),
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEMS_YA_REGISTRADOS
        );
    }
}
