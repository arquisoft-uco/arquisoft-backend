package com.arquisoft.seguridad.application.auth.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.message.constant.SeguridadCodes;

public class AutenticacionException extends ApplicationException {

    public AutenticacionException(String message) {
        super(BaseError.of(SeguridadCodes.Autenticacion.AUTENTICACION_ERROR, message));
    }

    public AutenticacionException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Autenticacion.AUTENTICACION_ERROR, message, cause), cause);
    }

    protected AutenticacionException(BaseError error) {
        super(error);
    }

    protected AutenticacionException(BaseError error, Throwable cause) {
        super(error, cause);
    }
}
