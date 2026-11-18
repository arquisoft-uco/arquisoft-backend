package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;

import java.util.UUID;

public final class ItemCuantitativoAsesorNoEncontradoException extends DomainException {

    public ItemCuantitativoAsesorNoEncontradoException(UUID itemCuantitativoAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO,
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_NO_ENCONTRADO_DETALLE, itemCuantitativoAsesor)
        ));
    }
}
