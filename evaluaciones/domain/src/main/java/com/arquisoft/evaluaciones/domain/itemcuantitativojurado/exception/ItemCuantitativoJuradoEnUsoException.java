package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;

import java.util.UUID;

public final class ItemCuantitativoJuradoEnUsoException extends DomainException {

    public ItemCuantitativoJuradoEnUsoException(UUID itemCuantitativoJurado) {
        super(
                Mensajes.formatear(ItemCuantitativoJuradoKey.ERROR_EN_USO, itemCuantitativoJurado),
                EvaluacionesCodes.ItemCuantitativoJurado.ITEM_EN_USO
        );
    }
}
