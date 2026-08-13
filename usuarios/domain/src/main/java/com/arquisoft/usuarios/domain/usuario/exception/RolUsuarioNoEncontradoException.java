package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;

public final class RolUsuarioNoEncontradoException extends DomainException {

    public RolUsuarioNoEncontradoException(String codigo) {
        super(
                Mensajes.formatear(UsuarioKey.ERROR_ROL_NO_ENCONTRADO, codigo),
                UsuariosCodes.Usuario.ROL_NO_ENCONTRADO
        );
    }
}
