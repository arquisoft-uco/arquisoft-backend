package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class CancelacionEntregableNoPermitidaException extends DomainException {

    public CancelacionEntregableNoPermitidaException(UUID entregableProyectoGrado, EstadoEntregable actual) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.CANCELACION_NO_PERMITIDA,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_CANCELACION_NO_PERMITIDA, actual.getNombre()),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_CANCELACION_NO_PERMITIDA_DETALLE,
                        entregableProyectoGrado, actual.getId())
        ));
    }
}
