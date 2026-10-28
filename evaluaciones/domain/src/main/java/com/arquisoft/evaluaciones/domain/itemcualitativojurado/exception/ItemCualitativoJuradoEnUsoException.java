package com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;

import java.util.UUID;

public final class ItemCualitativoJuradoEnUsoException extends DomainException {

    public ItemCualitativoJuradoEnUsoException(UUID itemCualitativoJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCualitativoJurado.ITEM_EN_USO,
                Mensajes.formatear(ItemCualitativoJuradoKey.ERROR_EN_USO),
                Mensajes.formatear(ItemCualitativoJuradoKey.ERROR_EN_USO_DETALLE, itemCualitativoJurado)
        ));
    }
}
