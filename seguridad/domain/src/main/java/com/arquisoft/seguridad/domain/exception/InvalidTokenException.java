package com.arquisoft.seguridad.domain.exception;

import com.arquisoft.shared.exception.BaseError;

/**
 * Excepción para tokens JWT inválidos, malformados o expirados.
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String message) {
        super(BaseError.of("TOKEN_INVALIDO", message));
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(BaseError.of("TOKEN_INVALIDO", message, cause), cause);
    }
}
