package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;

import java.util.UUID;

public final class AdministradorAutoeliminacionException extends DomainException {

    public AdministradorAutoeliminacionException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Administrador.ADMINISTRADOR_AUTOELIMINACION,
                Mensajes.formatear(RemoverAdministradorKey.ERROR_AUTOELIMINACION),
                Mensajes.formatear(RemoverAdministradorKey.ERROR_AUTOELIMINACION_DETALLE, usuario)
        ));
    }
}
