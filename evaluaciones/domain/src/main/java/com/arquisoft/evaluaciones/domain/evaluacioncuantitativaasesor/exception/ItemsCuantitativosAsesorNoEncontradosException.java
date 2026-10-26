package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class ItemsCuantitativosAsesorNoEncontradosException extends DomainException {

    public ItemsCuantitativosAsesorNoEncontradosException(Set<UUID> ids) {
        super(
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_ITEMS_NO_ENCONTRADOS, ids),
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEMS_NO_ENCONTRADOS
        );
    }
}
