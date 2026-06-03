package com.arquisoft.seguridad.application.auth.query;

import com.arquisoft.seguridad.application.auth.query.criteria.ValidateTokenCriteria;
import com.arquisoft.seguridad.application.auth.query.model.TokenInfoDTO;
import com.arquisoft.seguridad.application.auth.query.port.in.ValidateTokenInputPort;
import com.arquisoft.seguridad.application.auth.query.port.out.TokenQueryOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementacion del caso de uso de validacion de token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateTokenUseCase implements ValidateTokenInputPort {

    private final TokenQueryOutputPort tokenQueryOutputPort;

    @Override
    public ValidationResult ejecutar(ValidateTokenCriteria criteria) {
        log.debug("Intento de validacion de token");

        try {
            if (tokenQueryOutputPort.validateToken(criteria.token())) {
                TokenInfoDTO info = tokenQueryOutputPort.extractUserInfo(criteria.token());

                return new ValidationResult(
                        true,
                        info.keycloakUserId(),
                        info.email(),
                        "Token valido"
                );
            } else {
                return new ValidationResult(false, null, null, "Token invalido o expirado");
            }
        } catch (Exception e) {
            log.debug("Validacion de token fallida: {}", e.getMessage());
            return new ValidationResult(false, null, null, "Error al validar token: " + e.getMessage());
        }
    }
}
