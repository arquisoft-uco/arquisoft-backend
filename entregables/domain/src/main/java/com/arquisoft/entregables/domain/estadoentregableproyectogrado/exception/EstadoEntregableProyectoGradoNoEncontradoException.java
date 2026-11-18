package com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableProyectoGradoKey;

import java.util.UUID;

public final class EstadoEntregableProyectoGradoNoEncontradoException extends DomainException {

    public EstadoEntregableProyectoGradoNoEncontradoException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.EstadoEntregableProyectoGrado.NO_ENCONTRADO,
                Mensajes.formatear(EstadoEntregableProyectoGradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoEntregableProyectoGradoKey.ERROR_NO_ENCONTRADO_DETALLE,
                        entregableProyectoGrado)
        ));
    }
}
