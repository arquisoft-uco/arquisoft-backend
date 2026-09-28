package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;

import java.util.UUID;

public final class AdministradorUsuarioDuplicadoException extends DomainException {

    public AdministradorUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarAdministradorKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.Administrador.USUARIO_DUPLICADO
        );
    }
}
