package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class EntregableProyectoGradoNoEncontradoException extends DomainException {

    public EntregableProyectoGradoNoEncontradoException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.NO_ENCONTRADO,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_NO_ENCONTRADO_DETALLE, entregableProyectoGrado)
        ));
    }
}
