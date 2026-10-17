package com.arquisoft.proyectos.domain.estadoasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;

public final class EstadoAsesorNoEncontradoException extends DomainException {

    public EstadoAsesorNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EstadoAsesorKey.ERROR_NO_ENCONTRADO, id),
                ProyectosCodes.EstadoAsesor.NO_ENCONTRADO
        );
    }
}
