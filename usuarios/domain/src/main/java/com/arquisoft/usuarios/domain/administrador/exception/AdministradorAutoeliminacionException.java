package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;

import java.util.UUID;

public final class AdministradorAutoeliminacionException extends DomainException {

    public AdministradorAutoeliminacionException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverAdministradorKey.ERROR_AUTOELIMINACION, usuario),
                UsuariosCodes.Administrador.ADMINISTRADOR_AUTOELIMINACION
        );
    }
}
