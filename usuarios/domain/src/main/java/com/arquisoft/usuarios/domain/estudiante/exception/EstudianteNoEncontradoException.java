package com.arquisoft.usuarios.domain.estudiante.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverEstudianteKey;

import java.util.UUID;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO,
                Mensajes.formatear(RemoverEstudianteKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(RemoverEstudianteKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
