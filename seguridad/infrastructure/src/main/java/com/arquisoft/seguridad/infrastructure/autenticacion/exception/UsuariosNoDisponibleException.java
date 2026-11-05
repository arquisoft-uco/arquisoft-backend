package com.arquisoft.seguridad.infrastructure.autenticacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.constant.SeguridadCodes;

public final class UsuariosNoDisponibleException extends InfrastructureException {

    public UsuariosNoDisponibleException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Autenticacion.USUARIOS_NO_DISPONIBLE, message, cause), cause);
    }
}
