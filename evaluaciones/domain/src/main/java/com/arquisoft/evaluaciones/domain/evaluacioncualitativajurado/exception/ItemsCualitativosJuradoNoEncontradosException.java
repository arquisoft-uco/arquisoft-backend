package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class ItemsCualitativosJuradoNoEncontradosException extends DomainException {

    public ItemsCualitativosJuradoNoEncontradosException(Set<UUID> items) {
        super(
                Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_ITEMS_NO_ENCONTRADOS, items),
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEMS_NO_ENCONTRADOS
        );
    }
}
