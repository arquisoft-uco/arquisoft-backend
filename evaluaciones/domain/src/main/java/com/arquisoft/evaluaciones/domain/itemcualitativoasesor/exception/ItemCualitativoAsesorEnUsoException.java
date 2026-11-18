package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;

import java.util.UUID;

public final class ItemCualitativoAsesorEnUsoException extends DomainException {

    public ItemCualitativoAsesorEnUsoException(UUID itemCualitativoAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_EN_USO,
                Mensajes.formatear(ItemCualitativoAsesorKey.ERROR_EN_USO),
                Mensajes.formatear(ItemCualitativoAsesorKey.ERROR_EN_USO_DETALLE, itemCualitativoAsesor)
        ));
    }
}
