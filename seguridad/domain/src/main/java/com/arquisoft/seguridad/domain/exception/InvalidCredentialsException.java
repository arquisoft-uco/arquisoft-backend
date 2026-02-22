package com.arquisoft.seguridad.domain.exception;

/**
 * Excepción para credenciales inválidas
 */
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
