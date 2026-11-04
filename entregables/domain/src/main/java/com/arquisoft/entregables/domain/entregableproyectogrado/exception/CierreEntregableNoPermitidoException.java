package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class CierreEntregableNoPermitidoException extends DomainException {

    public CierreEntregableNoPermitidoException(UUID entregableProyectoGrado, EstadoEntregable actual) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.CIERRE_NO_PERMITIDO,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_CIERRE_NO_PERMITIDO, actual.getNombre()),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_CIERRE_NO_PERMITIDO_DETALLE,
                        entregableProyectoGrado, actual.getId())
        ));
    }
}
