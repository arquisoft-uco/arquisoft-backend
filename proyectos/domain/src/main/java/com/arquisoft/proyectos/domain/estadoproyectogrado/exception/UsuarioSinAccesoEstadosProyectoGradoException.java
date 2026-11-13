package com.arquisoft.proyectos.domain.estadoproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;

import java.util.UUID;

public final class UsuarioSinAccesoEstadosProyectoGradoException extends DomainException {

    public UsuarioSinAccesoEstadosProyectoGradoException(UUID proyectoGrado, UUID usuario) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstadoProyectoGrado.USUARIO_SIN_ACCESO,
                Mensajes.formatear(EstadoProyectoGradoKey.ERROR_USUARIO_SIN_ACCESO),
                Mensajes.formatear(EstadoProyectoGradoKey.ERROR_USUARIO_SIN_ACCESO_DETALLE, usuario, proyectoGrado)
        ));
    }
}
