package com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;

public final class NombreItemCualitativoJuradoDuplicadoException extends DomainException {

    public NombreItemCualitativoJuradoDuplicadoException(String nombre) {
        super(
                Mensajes.formatear(ItemCualitativoJuradoKey.ERROR_NOMBRE_DUPLICADO, nombre),
                EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DUPLICADO
        );
    }
}
