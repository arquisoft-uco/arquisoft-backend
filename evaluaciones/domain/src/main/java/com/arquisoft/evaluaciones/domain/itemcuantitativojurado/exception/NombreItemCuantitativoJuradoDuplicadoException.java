package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;

import java.util.UUID;

public final class NombreItemCuantitativoJuradoDuplicadoException extends DomainException {

    public NombreItemCuantitativoJuradoDuplicadoException(String nombre, UUID categoria) {
        super(
                Mensajes.formatear(
                        ItemCuantitativoJuradoKey.ERROR_NOMBRE_CATEGORIA_DUPLICADO,
                        nombre,
                        categoria),
                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_CATEGORIA_DUPLICADO);
    }
}
