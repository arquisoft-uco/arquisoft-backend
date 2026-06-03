package com.arquisoft.seguridad.application.auth.query.port.in;

import com.arquisoft.seguridad.application.auth.query.criteria.ValidateTokenCriteria;
import com.arquisoft.shared.inputport.InputPort;

/**
 * Puerto de entrada — caso de uso para validar un token JWT.
 */
public interface ValidateTokenInputPort
        extends InputPort<ValidateTokenCriteria, ValidateTokenInputPort.ValidationResult> {

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
