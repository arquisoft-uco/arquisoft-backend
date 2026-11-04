package com.arquisoft.proyectos.domain.estadoasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;

public final class EstadoAsesorNoEncontradoException extends DomainException {

    public EstadoAsesorNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstadoAsesor.NO_ENCONTRADO,
                Mensajes.formatear(EstadoAsesorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoAsesorKey.ERROR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
