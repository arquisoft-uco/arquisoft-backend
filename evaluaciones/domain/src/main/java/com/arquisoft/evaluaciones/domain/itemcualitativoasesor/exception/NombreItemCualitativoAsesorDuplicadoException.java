package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;

public final class NombreItemCualitativoAsesorDuplicadoException extends DomainException {

    public NombreItemCualitativoAsesorDuplicadoException(String nombre) {
        super(BaseError.of(
                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DUPLICADO,
                Mensajes.formatear(ItemCualitativoAsesorKey.ERROR_NOMBRE_DUPLICADO, nombre)));
    }
}
