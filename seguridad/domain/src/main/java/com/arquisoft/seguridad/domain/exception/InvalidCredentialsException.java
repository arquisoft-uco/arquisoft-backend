package com.arquisoft.seguridad.domain.exception;

import com.arquisoft.shared.exceptions.BaseError;

/**
 * Excepción para credenciales inválidas (usuario/contraseña incorrectos).
 */
public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException(String message) {
        super(BaseError.of("CREDENCIALES_INVALIDAS", message));
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(BaseError.of("CREDENCIALES_INVALIDAS", message, cause), cause);
    }
}
