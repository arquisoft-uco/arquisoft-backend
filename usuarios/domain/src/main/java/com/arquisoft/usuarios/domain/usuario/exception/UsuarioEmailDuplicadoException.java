package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.UsuariosCodes;
import com.arquisoft.shared.message.UsuariosKeys;
import com.arquisoft.shared.exception.ApplicationException;

public final class UsuarioEmailDuplicadoException extends ApplicationException {

    public UsuarioEmailDuplicadoException(String email) {
        super(
                Messages.formatear(UsuariosKeys.Usuario.ERROR_EMAIL_DUPLICADO, email),
                UsuariosCodes.Usuario.USUARIO_EMAIL_DUPLICADO
        );
    }
}
