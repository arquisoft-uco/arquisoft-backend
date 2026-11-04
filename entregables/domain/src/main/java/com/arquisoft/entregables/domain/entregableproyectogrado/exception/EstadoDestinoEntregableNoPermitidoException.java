package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class EstadoDestinoEntregableNoPermitidoException extends DomainException {

    public EstadoDestinoEntregableNoPermitidoException(UUID entregableProyectoGrado, EstadoEntregable nuevo) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.ESTADO_DESTINO_NO_PERMITIDO,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, nuevo.getNombre()),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE,
                        entregableProyectoGrado, nuevo.getId())
        ));
    }
}
