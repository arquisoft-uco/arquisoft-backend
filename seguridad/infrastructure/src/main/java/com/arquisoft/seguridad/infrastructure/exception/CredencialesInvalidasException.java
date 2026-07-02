package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.message.SeguridadMessages;

public final class CredencialesInvalidasException extends AuthenticationException {

    public CredencialesInvalidasException(String message) {
        super(BaseError.of(SeguridadMessages.Login.CREDENCIALES_INVALIDAS, message));
    }

    public CredencialesInvalidasException(String message, Throwable cause) {
        super(BaseError.of(SeguridadMessages.Login.CREDENCIALES_INVALIDAS, message, cause), cause);
    }
}
