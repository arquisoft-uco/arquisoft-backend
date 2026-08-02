package com.arquisoft.seguridad.domain.auth.exception;

import com.arquisoft.shared.message.SeguridadCodes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

public class AuthenticationException extends DomainException {

    public AuthenticationException(String message) {
        super(BaseError.of(SeguridadCodes.Login.AUTENTICACION_ERROR, message));
    }

    public AuthenticationException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Login.AUTENTICACION_ERROR, message, cause), cause);
    }

    protected AuthenticationException(BaseError error) {
        super(error);
    }

    protected AuthenticationException(BaseError error, Throwable cause) {
        super(error, cause);
    }
}
