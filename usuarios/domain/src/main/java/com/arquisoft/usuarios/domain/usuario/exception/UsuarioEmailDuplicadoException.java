package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.UsuariosMessages;

public final class UsuarioEmailDuplicadoException extends ApplicationException {

    public UsuarioEmailDuplicadoException(String email) {
        super(
                UsuariosMessages.Usuario.EMAIL_DUPLICADO_MSG.formatted(email),
                UsuariosMessages.Usuario.USUARIO_EMAIL_DUPLICADO
        );
    }
}
