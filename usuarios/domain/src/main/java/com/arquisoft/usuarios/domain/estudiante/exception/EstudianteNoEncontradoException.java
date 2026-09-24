package com.arquisoft.usuarios.domain.estudiante.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverEstudianteKey;

import java.util.UUID;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverEstudianteKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO
        );
    }
}
