package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;

import java.util.UUID;

public final class ItemCuantitativoJuradoNoEncontradoException extends DomainException {

    public ItemCuantitativoJuradoNoEncontradoException(UUID itemCuantitativoJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO,
                Mensajes.formatear(ItemCuantitativoJuradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ItemCuantitativoJuradoKey.ERROR_NO_ENCONTRADO_DETALLE, itemCuantitativoJurado)
        ));
    }
}
