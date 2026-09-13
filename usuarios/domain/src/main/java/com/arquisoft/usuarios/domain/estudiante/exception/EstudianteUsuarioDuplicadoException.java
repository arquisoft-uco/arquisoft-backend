package com.arquisoft.usuarios.domain.estudiante.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;

import java.util.UUID;

public final class EstudianteUsuarioDuplicadoException extends DomainException {

    public EstudianteUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarEstudianteKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.Estudiante.USUARIO_DUPLICADO
        );
    }
}
