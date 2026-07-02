package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.message.SeguridadMessages;

public final class TokenInvalidoException extends AuthenticationException {

    public TokenInvalidoException(String message) {
        super(BaseError.of(SeguridadMessages.Token.TOKEN_INVALIDO_CODIGO, message));
    }

    public TokenInvalidoException(String message, Throwable cause) {
        super(BaseError.of(SeguridadMessages.Token.TOKEN_INVALIDO_CODIGO, message, cause), cause);
    }
}
