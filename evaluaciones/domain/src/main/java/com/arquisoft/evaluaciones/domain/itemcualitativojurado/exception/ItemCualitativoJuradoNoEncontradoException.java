package com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;

import java.util.UUID;

public final class ItemCualitativoJuradoNoEncontradoException extends DomainException {

    public ItemCualitativoJuradoNoEncontradoException(UUID itemCualitativoJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCualitativoJurado.ITEM_NO_ENCONTRADO,
                Mensajes.formatear(ItemCualitativoJuradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ItemCualitativoJuradoKey.ERROR_NO_ENCONTRADO_DETALLE, itemCualitativoJurado)
        ));
    }
}
