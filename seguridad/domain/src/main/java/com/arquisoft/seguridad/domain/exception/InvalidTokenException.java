package com.arquisoft.seguridad.domain.exception;

/**
 * Excepción para tokens inválidos o expirados
 */
public class InvalidTokenException extends AuthenticationException {
    
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
