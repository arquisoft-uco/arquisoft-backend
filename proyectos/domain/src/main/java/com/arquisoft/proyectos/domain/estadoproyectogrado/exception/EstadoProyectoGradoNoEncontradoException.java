package com.arquisoft.proyectos.domain.estadoproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;

import java.util.UUID;

public final class EstadoProyectoGradoNoEncontradoException extends DomainException {

    public EstadoProyectoGradoNoEncontradoException(UUID proyectoGrado) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO,
                Mensajes.formatear(EstadoProyectoGradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoProyectoGradoKey.ERROR_NO_ENCONTRADO_DETALLE, proyectoGrado)
        ));
    }
}
