package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class ItemsCualitativosAsesorNoEncontradosException extends DomainException {

    public ItemsCualitativosAsesorNoEncontradosException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEMS_NO_ENCONTRADOS,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_ITEMS_NO_ENCONTRADOS),
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_ITEMS_NO_ENCONTRADOS_DETALLE, ids)
        ));
    }
}
