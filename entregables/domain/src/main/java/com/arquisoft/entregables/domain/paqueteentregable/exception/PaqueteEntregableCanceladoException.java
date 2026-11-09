package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class PaqueteEntregableCanceladoException extends DomainException {

    public PaqueteEntregableCanceladoException(UUID entregableProyectoGrado, int version) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.CANCELADO,
                Mensajes.formatear(PaqueteEntregableKey.ERROR_CANCELADO, version),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_CANCELADO_DETALLE, entregableProyectoGrado)
        ));
    }
}
