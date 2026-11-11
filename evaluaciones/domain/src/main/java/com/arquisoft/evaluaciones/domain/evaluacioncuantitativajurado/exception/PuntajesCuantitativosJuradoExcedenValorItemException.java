package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class PuntajesCuantitativosJuradoExcedenValorItemException extends DomainException {

    public PuntajesCuantitativosJuradoExcedenValorItemException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJES_EXCEDEN_VALOR_ITEM,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM_DETALLE, ids)
        ));
    }
}
