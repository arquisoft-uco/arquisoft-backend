package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.Set;
import java.util.UUID;

public final class PuntajesCuantitativosAsesorExcedenValorItemException extends DomainException {

    public PuntajesCuantitativosAsesorExcedenValorItemException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJES_EXCEDEN_VALOR_ITEM,
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM),
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM_DETALLE, ids)
        ));
    }
}
