package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.domain.port.in.JwtTokenProvider;
import com.arquisoft.seguridad.domain.model.AuthenticatedUserDTO;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de JwtTokenProvider usando Spring Security OAuth2.
 * Valida y parsea tokens JWT emitidos por Keycloak.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    
    private final JwtDecoder jwtDecoder;

    @Override
    public AuthenticatedUserDTO extractUserFromToken(String token) {
        try {
            Jwt jwt = getJwt(token);
            return mapJwtToUser(jwt);
        } catch (Exception e) {
            log.error("Error extracting user from token: {}", e.getMessage());
            throw new InvalidTokenException("Token inválido: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Jwt getJwt(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            throw new InvalidTokenException("No se pudo decodificar el token JWT", e);
        }
    }

    private AuthenticatedUserDTO mapJwtToUser(Jwt jwt) {
        List<String> roles = extractRoles(jwt);

        return AuthenticatedUserDTO.builder()
                .keycloakUserId(jwt.getSubject())
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .roles(roles)
                .issuedAt(jwt.getIssuedAt() != null ? jwt.getIssuedAt().toEpochMilli() : null)
                .expiresAt(jwt.getExpiresAt() != null ? jwt.getExpiresAt().toEpochMilli() : null)
                .build();
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
