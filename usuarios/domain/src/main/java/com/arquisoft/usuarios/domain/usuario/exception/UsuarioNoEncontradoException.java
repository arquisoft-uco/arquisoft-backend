package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;

import java.util.UUID;

public final class UsuarioNoEncontradoException extends DomainException {

    public UsuarioNoEncontradoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.NO_ENCONTRADO,
                Mensajes.formatear(ModificarUsuarioKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ModificarUsuarioKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
