package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class EstadoEntregableTerminalException extends DomainException {

    public EstadoEntregableTerminalException(UUID entregableProyectoGrado, EstadoEntregable actual) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.ESTADO_TERMINAL,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ESTADO_TERMINAL, actual.getNombre()),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ESTADO_TERMINAL_DETALLE,
                        entregableProyectoGrado, actual.getId())
        ));
    }
}
