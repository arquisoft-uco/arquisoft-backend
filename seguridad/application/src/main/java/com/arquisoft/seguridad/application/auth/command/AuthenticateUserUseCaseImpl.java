package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.port.AuthenticationPort;
import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementacion del caso de uso de autenticacion.
 * Orquesta la llamada al puerto de salida y mapea el resultado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final AuthenticationPort authenticationPort;

    @Override
    public AuthResult authenticate(String email, String password) {
        log.debug(SeguridadApplicationMessages.AuthenticateUserUseCase.AUTENTICAR_DEBUG);

        Map<String, Object> tokenResponse = authenticationPort.authenticate(email, password);

        log.info(SeguridadApplicationMessages.AuthenticateUserUseCase.AUTENTICAR_EXITOSO);

        return new AuthResult(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                ((Number) tokenResponse.getOrDefault("expires_in", 3600)).longValue(),
                (String) tokenResponse.getOrDefault("token_type", "Bearer"),
                (String) tokenResponse.getOrDefault("scope", "")
        );
    }
}
