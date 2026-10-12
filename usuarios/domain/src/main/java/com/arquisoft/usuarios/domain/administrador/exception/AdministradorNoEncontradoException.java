package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;

import java.util.UUID;

public final class AdministradorNoEncontradoException extends DomainException {

    public AdministradorNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverAdministradorKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Administrador.ADMINISTRADOR_NO_ENCONTRADO
        );
    }
}
