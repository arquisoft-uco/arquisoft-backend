package com.arquisoft.seguridad.application.usecase;

import com.arquisoft.seguridad.domain.port.in.RefreshTokenUseCase;
import com.arquisoft.seguridad.domain.port.out.AuthenticationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementacion del caso de uso de refresco de token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final AuthenticationPort authenticationPort;

    @Override
    public RefreshResult refresh(String refreshToken) {
        log.debug("Intento de refresco de token");

        Map<String, Object> tokenResponse = authenticationPort.refreshToken(refreshToken);

        log.debug("Token refrescado exitosamente");

        return new RefreshResult(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                ((Number) tokenResponse.getOrDefault("expires_in", 3600)).longValue(),
                (String) tokenResponse.getOrDefault("token_type", "Bearer"),
                (String) tokenResponse.getOrDefault("scope", "")
        );
    }
}
