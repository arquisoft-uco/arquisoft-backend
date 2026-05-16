package com.arquisoft.seguridad.application.auth.query;

/**
 * Caso de uso para validar un token JWT.
 */
public interface ValidateTokenUseCase {

    /**
     * Resultado de la validacion del token.
     */
    record ValidationResult(
            boolean valid,
            String keycloakUserId,
            String email,
            String message
    ) {}

    /**
     * Valida un token JWT y extrae informacion basica del usuario.
     *
     * @param token el token JWT a validar
     * @return resultado de la validacion con informacion del usuario si es valido
     */
    ValidationResult validate(String token);
}
