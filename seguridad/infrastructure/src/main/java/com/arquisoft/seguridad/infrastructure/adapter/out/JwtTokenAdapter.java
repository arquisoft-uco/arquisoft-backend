package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.domain.port.out.TokenPort;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador de salida que implementa TokenPort usando Spring Security OAuth2.
 * Valida y parsea tokens JWT emitidos por Keycloak.
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
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("keycloakUserId", jwt.getSubject());
        userInfo.put("email", jwt.getClaimAsString("email"));
        userInfo.put("name", jwt.getClaimAsString("name"));
        userInfo.put("roles", extractRoles(jwt));
        userInfo.put("issuedAt", jwt.getIssuedAt() != null ? jwt.getIssuedAt().toEpochMilli() : null);
        userInfo.put("expiresAt", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toEpochMilli() : null);
        return userInfo;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        Object rolesObj = jwt.getClaim("roles");
        if (rolesObj instanceof List) {
            roles.addAll((List<String>) rolesObj);
        }

        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Object realmRoles = ((Map<?, ?>) realmAccess).get("roles");
            if (realmRoles instanceof List) {
                roles.addAll((List<String>) realmRoles);
            }
        }

        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map) {
            Map<?, ?> resourceMap = (Map<?, ?>) resourceAccess;
            for (Object value : resourceMap.values()) {
                if (value instanceof Map) {
                    Object clientRoles = ((Map<?, ?>) value).get("roles");
                    if (clientRoles instanceof List) {
                        roles.addAll((List<String>) clientRoles);
                    }
                }
            }
        }

        return roles.stream()
                .distinct()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());
    }
}
