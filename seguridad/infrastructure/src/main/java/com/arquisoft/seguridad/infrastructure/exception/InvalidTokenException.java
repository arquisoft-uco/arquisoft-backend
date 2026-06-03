package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.shared.exception.BaseError;

/**
 * Excepción para tokens JWT inválidos, malformados o expirados.
 * Vive en infraestructura porque referencia conceptos de JWT/Keycloak.
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String message) {
        super(BaseError.of("TOKEN_INVALIDO", message));
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(BaseError.of("TOKEN_INVALIDO", message, cause), cause);
    }
}
