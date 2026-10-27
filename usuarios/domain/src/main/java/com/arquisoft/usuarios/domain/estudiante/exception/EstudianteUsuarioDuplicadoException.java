package com.arquisoft.usuarios.domain.estudiante.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;

import java.util.UUID;

public final class EstudianteUsuarioDuplicadoException extends DomainException {

    public EstudianteUsuarioDuplicadoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Estudiante.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarEstudianteKey.ERROR_USUARIO_DUPLICADO),
                Mensajes.formatear(AgregarEstudianteKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
