package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.exception.DomainException;

public final class UsuarioEmailDuplicadoException extends DomainException {

    public UsuarioEmailDuplicadoException(String email) {
        super(
                Mensajes.formatear(UsuarioKey.ERROR_EMAIL_DUPLICADO, email),
                UsuariosCodes.Usuario.USUARIO_EMAIL_DUPLICADO
        );
    }
}
