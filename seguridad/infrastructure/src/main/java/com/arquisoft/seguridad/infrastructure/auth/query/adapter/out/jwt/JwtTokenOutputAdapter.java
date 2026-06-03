package com.arquisoft.seguridad.infrastructure.auth.query.adapter.out.jwt;

import com.arquisoft.seguridad.application.auth.query.model.TokenInfoDTO;
import com.arquisoft.seguridad.application.auth.query.port.out.TokenQueryOutputPort;
import com.arquisoft.seguridad.infrastructure.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Los roles se leen exclusivamente de realm_access.roles (ADR-003).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenOutputAdapter implements TokenQueryOutputPort {

    private final JwtDecoder jwtDecoder;

    @Override
    public TokenInfoDTO extractUserInfo(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return new TokenInfoDTO(
                    jwt.getSubject(),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("name"),
                    extractRealmRoles(jwt),
                    jwt.getIssuedAt()  != null ? jwt.getIssuedAt().toEpochMilli()  : 0L,
                    jwt.getExpiresAt() != null ? jwt.getExpiresAt().toEpochMilli() : 0L
            );
        } catch (Exception e) {
            log.error("Error al extraer informacion del token: {}", e.getMessage());
            throw new InvalidTokenException("Token invalido: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            log.warn("Validacion de token fallida: {}", e.getMessage());
            return false;
        }
    }

    private List<String> extractRealmRoles(Jwt jwt) {
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
