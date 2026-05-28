package com.arquisoft.seguridad.application.auth.query;

import com.arquisoft.shared.inputport.InputPort;

/**
 * Puerto de entrada — caso de uso para validar un token JWT.
 */
public interface ValidateTokenInputPort
        extends InputPort<String, ValidateTokenInputPort.ValidationResult> {

    /**
     * Resultado de la validacion del token.
     */
    record ValidationResult(
            boolean valid,
            String keycloakUserId,
            String email,
            String message
    ) {}
}
