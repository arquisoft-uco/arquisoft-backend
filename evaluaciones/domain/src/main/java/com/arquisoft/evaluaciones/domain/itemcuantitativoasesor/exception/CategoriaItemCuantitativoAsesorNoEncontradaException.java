package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;

import java.util.UUID;

public final class CategoriaItemCuantitativoAsesorNoEncontradaException extends DomainException {

    public CategoriaItemCuantitativoAsesorNoEncontradaException(UUID categoria) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_NO_ENCONTRADA,
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_CATEGORIA_NO_ENCONTRADA),
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_CATEGORIA_NO_ENCONTRADA_DETALLE, categoria)
        ));
    }
}
