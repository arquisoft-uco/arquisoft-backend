package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;

import java.util.UUID;

public final class NombreItemCuantitativoAsesorDuplicadoException extends DomainException {

    public NombreItemCuantitativoAsesorDuplicadoException(String nombre, UUID categoria) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_CATEGORIA_DUPLICADO,
                Mensajes.formatear(ItemCuantitativoAsesorKey.ERROR_NOMBRE_CATEGORIA_DUPLICADO, nombre),
                Mensajes.formatear(
                        ItemCuantitativoAsesorKey.ERROR_NOMBRE_CATEGORIA_DUPLICADO_DETALLE, nombre, categoria)
        ));
    }
}
