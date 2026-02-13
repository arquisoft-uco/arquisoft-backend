package com.arquisoft.shared.security.domain.exceptions;

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
