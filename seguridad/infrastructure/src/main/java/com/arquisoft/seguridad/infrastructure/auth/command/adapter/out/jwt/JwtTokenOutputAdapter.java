package com.arquisoft.seguridad.infrastructure.auth.command.adapter.out.jwt;

import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.port.out.TokenValidationOutputPort;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
import com.arquisoft.shared.message.SeguridadMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Los roles se leen exclusivamente de realm_access.roles (ADR-003).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenOutputAdapter implements TokenValidationOutputPort {

    private final JwtDecoder jwtDecoder;

    @Override
    public IdentidadToken extraerInfo(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return IdentidadToken.de(
                    jwt.getSubject(),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("name"),
                    extraerRolesRealm(jwt)
            );
        } catch (Exception e) {
            log.error(SeguridadMessages.Token.LOG_ERROR_EXTRAER_INFO, e.getMessage());
            throw new TokenInvalidoException(SeguridadMessages.Token.TOKEN_INVALIDO_PREFIJO + e.getMessage(), e);
        }
    }

    @Override
    public boolean validarToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            log.warn(SeguridadMessages.Token.LOG_VALIDACION_FALLIDA, e.getMessage());
            return false;
        }
    }

    private List<String> extraerRolesRealm(Jwt jwt) {
        if (jwt.getClaim("realm_access") instanceof Map<?, ?> realmAccess
                && realmAccess.get("roles") instanceof List<?> rawRoles) {
            return rawRoles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }
}
