package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.shared.exception.BaseError;

/**
 * Excepción para credenciales inválidas (usuario/contraseña incorrectos).
 * Vive en infraestructura porque referencia conceptos del proveedor de identidad.
 */
public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException(String message) {
        super(BaseError.of("CREDENCIALES_INVALIDAS", message));
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(BaseError.of("CREDENCIALES_INVALIDAS", message, cause), cause);
    }
}
