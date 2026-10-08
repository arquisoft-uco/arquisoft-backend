package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;

import java.util.UUID;

public final class UsuarioNoEncontradoException extends DomainException {

    public UsuarioNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(ModificarUsuarioKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Usuario.NO_ENCONTRADO
        );
    }
}
