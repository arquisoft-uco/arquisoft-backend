package com.arquisoft.usuarios.domain.representantecomitecurriculum.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RepresentanteComiteCurriculumKey;

import java.util.UUID;

public final class UsuarioNoEncontradoException extends DomainException {

    public UsuarioNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(
                        RepresentanteComiteCurriculumKey.ERROR_USUARIO_NO_ENCONTRADO,
                        usuario
                ),
                UsuariosCodes.RepresentanteComiteCurriculum.USUARIO_NO_ENCONTRADO
        );
    }
}
