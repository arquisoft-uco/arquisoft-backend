package com.arquisoft.proyectos.domain.estadoproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;

public final class EstadoProyectoGradoNoEncontradoException extends DomainException {

    public EstadoProyectoGradoNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EstadoProyectoGradoKey.ERROR_NO_ENCONTRADO, id),
                ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO
        );
    }
}
