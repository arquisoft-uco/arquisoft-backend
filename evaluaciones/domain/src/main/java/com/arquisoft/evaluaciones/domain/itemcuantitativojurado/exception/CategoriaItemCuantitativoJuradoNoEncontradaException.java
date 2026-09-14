package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;

import java.util.UUID;

public final class CategoriaItemCuantitativoJuradoNoEncontradaException extends DomainException {

    public CategoriaItemCuantitativoJuradoNoEncontradaException(UUID categoria) {
        super(
                Mensajes.formatear(
                        ItemCuantitativoJuradoKey.ERROR_CATEGORIA_NO_ENCONTRADA,
                        categoria),
                EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_NO_ENCONTRADA);
    }
}
