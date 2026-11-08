package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class PaqueteEntregableEnPreparacionException extends DomainException {

    public PaqueteEntregableEnPreparacionException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.EN_PREPARACION,
                Mensajes.obtener(PaqueteEntregableKey.ERROR_EN_PREPARACION),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_EN_PREPARACION_DETALLE, entregableProyectoGrado)
        ));
    }
}
