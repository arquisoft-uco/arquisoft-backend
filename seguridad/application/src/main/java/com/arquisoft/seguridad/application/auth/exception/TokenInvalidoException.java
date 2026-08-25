package com.arquisoft.seguridad.application.auth.exception;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.BaseError;

public final class TokenInvalidoException extends AutenticacionException {

    public TokenInvalidoException(String message) {
        super(BaseError.of(SeguridadCodes.Token.TOKEN_INVALIDO, message));
    }

    public TokenInvalidoException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Token.TOKEN_INVALIDO, message, cause), cause);
    }
}
