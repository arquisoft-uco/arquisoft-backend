package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class ItemsCuantitativosJuradoNoEncontradosException extends DomainException {

    public ItemsCuantitativosJuradoNoEncontradosException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEMS_NO_ENCONTRADOS,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_ITEMS_NO_ENCONTRADOS),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_ITEMS_NO_ENCONTRADOS_DETALLE, ids)
        ));
    }
}
