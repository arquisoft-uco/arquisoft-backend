package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.domain.port.out.TokenPort;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de salida que implementa TokenPort usando Spring Security OAuth2.
 * Valida y parsea tokens JWT emitidos por Keycloak.
 * Los roles se leen exclusivamente de realm_access.roles (ADR-003).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenPort {

    private final JwtDecoder jwtDecoder;

    @Override
    public Map<String, Object> extractUserInfo(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return mapJwtToUserInfo(jwt);
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

    private Map<String, Object> mapJwtToUserInfo(Jwt jwt) {
        return Map.of(
                "keycloakUserId", jwt.getSubject(),
                "email",          String.valueOf(jwt.getClaimAsString("email")),
                "name",           String.valueOf(jwt.getClaimAsString("name")),
                "roles",          extractRealmRoles(jwt),
                "issuedAt",       jwt.getIssuedAt()  != null ? jwt.getIssuedAt().toEpochMilli()  : 0L,
                "expiresAt",      jwt.getExpiresAt() != null ? jwt.getExpiresAt().toEpochMilli() : 0L
        );
    }

    /**
     * Extrae los roles de realm_access.roles únicamente.
     * Usa instanceof pattern matching (Java 16+) — sin @SuppressWarnings.
     */
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
