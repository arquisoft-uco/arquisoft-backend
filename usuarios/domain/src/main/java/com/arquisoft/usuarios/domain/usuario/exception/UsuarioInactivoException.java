package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;

import java.util.UUID;

public final class UsuarioInactivoException extends DomainException {

    public UsuarioInactivoException(UUID usuario) {
        super(
                Mensajes.formatear(ModificarUsuarioKey.ERROR_INACTIVO, usuario),
                UsuariosCodes.Usuario.INACTIVO
        );
    }
}
