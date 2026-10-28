package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCualitativasAsesorDuplicadasException extends DomainException {

    public EvaluacionesCualitativasAsesorDuplicadasException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEMS_YA_REGISTRADOS,
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_ITEMS_YA_REGISTRADOS),
                Mensajes.formatear(EvaluacionCualitativaAsesorKey.ERROR_ITEMS_YA_REGISTRADOS_DETALLE, ids)
        ));
    }
}
