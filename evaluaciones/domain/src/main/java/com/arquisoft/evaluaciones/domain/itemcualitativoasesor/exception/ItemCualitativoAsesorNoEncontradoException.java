package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;

import java.util.UUID;

public final class ItemCualitativoAsesorNoEncontradoException extends DomainException {

    public ItemCualitativoAsesorNoEncontradoException(UUID itemCualitativoAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO,
                Mensajes.formatear(ItemCualitativoAsesorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ItemCualitativoAsesorKey.ERROR_NO_ENCONTRADO_DETALLE, itemCualitativoAsesor)
        ));
    }
}
