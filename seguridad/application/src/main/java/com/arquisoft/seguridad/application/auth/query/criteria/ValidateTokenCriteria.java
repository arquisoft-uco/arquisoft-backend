package com.arquisoft.seguridad.application.auth.query.criteria;

/**
 * Criterio para el caso de uso de validacion de token.
 * Encapsula el token a validar como tipo de aplicacion puro.
 */
public record ValidateTokenCriteria(String token) {

    public ValidateTokenCriteria {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token a validar no puede ser nulo ni vacio");
        }
    }
}
