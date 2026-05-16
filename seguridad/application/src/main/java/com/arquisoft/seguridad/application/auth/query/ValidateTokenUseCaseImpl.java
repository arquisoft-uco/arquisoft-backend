package com.arquisoft.seguridad.application.auth.query;

import com.arquisoft.seguridad.application.auth.port.TokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementacion del caso de uso de validacion de token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

    private final TokenPort tokenPort;

    @Override
    public ValidationResult validate(String token) {
        log.debug("Intento de validacion de token");

        try {
            if (tokenPort.validateToken(token)) {
                Map<String, Object> userInfo = tokenPort.extractUserInfo(token);

                return new ValidationResult(
                        true,
                        (String) userInfo.get("keycloakUserId"),
                        (String) userInfo.get("email"),
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
