package com.arquisoft.proyectos.domain.estadoproyecto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoKey;

public final class EstadoProyectoNoEncontradoException extends DomainException {

    public EstadoProyectoNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstadoProyecto.NO_ENCONTRADO,
                Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
