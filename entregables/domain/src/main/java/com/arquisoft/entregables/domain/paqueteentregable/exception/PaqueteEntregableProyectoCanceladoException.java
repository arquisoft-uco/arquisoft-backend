package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class PaqueteEntregableProyectoCanceladoException extends DomainException {

    public PaqueteEntregableProyectoCanceladoException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.PROYECTO_CANCELADO,
                Mensajes.formatear(PaqueteEntregableKey.ERROR_PROYECTO_CANCELADO),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_PROYECTO_CANCELADO_DETALLE, entregableProyectoGrado)
        ));
    }
}
