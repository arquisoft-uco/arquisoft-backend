package com.arquisoft.seguridad.application.usecase;

import com.arquisoft.seguridad.domain.event.UsuarioAutenticadoEvent;
import com.arquisoft.seguridad.domain.port.in.AuthenticateUserUseCase;
import com.arquisoft.seguridad.domain.port.out.AuthenticationPort;
import com.arquisoft.shared.events.EventPublisher;
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
    private final EventPublisher eventPublisher;

    @Override
    public AuthResult authenticate(String email, String password) {
        log.debug("Intento de autenticacion");

        Map<String, Object> tokenResponse = authenticationPort.authenticate(email, password);

        log.info("Autenticacion exitosa");
        eventPublisher.publish(new UsuarioAutenticadoEvent(email));

        return new AuthResult(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                ((Number) tokenResponse.getOrDefault("expires_in", 3600)).longValue(),
                (String) tokenResponse.getOrDefault("token_type", "Bearer"),
                (String) tokenResponse.getOrDefault("scope", "")
        );
    }
}
