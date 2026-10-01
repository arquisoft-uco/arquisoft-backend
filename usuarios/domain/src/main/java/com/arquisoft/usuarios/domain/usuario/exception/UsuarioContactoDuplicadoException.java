package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;

public final class UsuarioContactoDuplicadoException extends DomainException {

    public UsuarioContactoDuplicadoException(String contacto) {
        super(
                Mensajes.formatear(RegistrarUsuarioKey.ERROR_CONTACTO_DUPLICADO, contacto),
                UsuariosCodes.Usuario.CONTACTO_DUPLICADO
        );
    }
}
