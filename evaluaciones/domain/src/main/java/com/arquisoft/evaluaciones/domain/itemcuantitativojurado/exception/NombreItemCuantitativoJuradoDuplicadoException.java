package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;

import java.util.UUID;

public final class NombreItemCuantitativoJuradoDuplicadoException extends DomainException {

    public NombreItemCuantitativoJuradoDuplicadoException(String nombre, UUID categoria) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_CATEGORIA_DUPLICADO,
                Mensajes.formatear(ItemCuantitativoJuradoKey.ERROR_NOMBRE_CATEGORIA_DUPLICADO, nombre),
                Mensajes.formatear(ItemCuantitativoJuradoKey.ERROR_NOMBRE_CATEGORIA_DUPLICADO_DETALLE, nombre, categoria)
        ));
    }
}
