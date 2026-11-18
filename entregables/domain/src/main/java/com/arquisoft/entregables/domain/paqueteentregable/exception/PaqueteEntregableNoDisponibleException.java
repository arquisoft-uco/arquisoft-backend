package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class PaqueteEntregableNoDisponibleException extends DomainException {

    public PaqueteEntregableNoDisponibleException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.NO_DISPONIBLE,
                Mensajes.obtener(PaqueteEntregableKey.ERROR_NO_DISPONIBLE),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_NO_DISPONIBLE_DETALLE, entregableProyectoGrado)
        ));
    }
}
