package com.arquisoft.shared.security.domain.exceptions;

/**
 * Excepción base para errores de autenticación
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
