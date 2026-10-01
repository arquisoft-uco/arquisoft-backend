package com.arquisoft.seguridad.application.auth.exception;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.BaseError;

public final class CredencialesInvalidasException extends AutenticacionException {

    public CredencialesInvalidasException(String message) {
        super(BaseError.of(SeguridadCodes.Autenticacion.CREDENCIALES_INVALIDAS, message));
    }

    public CredencialesInvalidasException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Autenticacion.CREDENCIALES_INVALIDAS, message, cause), cause);
    }
}
