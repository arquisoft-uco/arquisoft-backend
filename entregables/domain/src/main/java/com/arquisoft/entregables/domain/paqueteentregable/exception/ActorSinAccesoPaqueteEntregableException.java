package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class ActorSinAccesoPaqueteEntregableException extends DomainException {

    public ActorSinAccesoPaqueteEntregableException(UUID actor, UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.ACCESO_DENEGADO,
                Mensajes.obtener(PaqueteEntregableKey.ERROR_ACCESO_DENEGADO),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_ACCESO_DENEGADO_DETALLE, actor, entregableProyectoGrado)
        ));
    }
}
