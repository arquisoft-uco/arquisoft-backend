package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;

import java.util.UUID;

public final class ItemCuantitativoAsesorEnUsoException extends DomainException {

    public ItemCuantitativoAsesorEnUsoException(UUID itemCuantitativoAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_EN_USO,
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_EN_USO),
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_EN_USO_DETALLE, itemCuantitativoAsesor)
        ));
    }
}
