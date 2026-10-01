package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;

public final class UsuarioEmailDuplicadoException extends DomainException {

    public UsuarioEmailDuplicadoException(String email) {
        super(
                Mensajes.formatear(RegistrarUsuarioKey.ERROR_EMAIL_DUPLICADO, email),
                UsuariosCodes.Usuario.EMAIL_DUPLICADO
        );
    }
}
