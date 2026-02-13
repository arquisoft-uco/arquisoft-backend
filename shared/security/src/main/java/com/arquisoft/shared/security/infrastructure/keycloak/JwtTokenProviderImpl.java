package com.arquisoft.shared.security.infrastructure.keycloak;

import com.arquisoft.shared.security.application.services.JwtTokenProvider;
import com.arquisoft.shared.security.domain.dto.AuthenticatedUserDTO;
import com.arquisoft.shared.security.domain.exceptions.InvalidTokenException;
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

    /**
     * Mapea un JWT a un DTO de usuario autenticado.
     */
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

    /**
     * Extrae los roles del token JWT.
     * Los roles pueden venir en diferentes formatos dependiendo de la configuración de Keycloak:
     * - En la propiedad "roles" (array simple)
     * - En la propiedad "realm_access" -> "roles" (anidado)
     * - En la propiedad "resource_access" -> "client_id" -> "roles" (por cliente)
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        // Intenta obtener roles del claim "roles"
        Object rolesObj = jwt.getClaim("roles");
        if (rolesObj instanceof List) {
            roles.addAll((List<String>) rolesObj);
        }

        // Intenta obtener roles de "realm_access"
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Object realmRoles = ((Map<?, ?>) realmAccess).get("roles");
            if (realmRoles instanceof List) {
                roles.addAll((List<String>) realmRoles);
            }
        }

        // Intenta obtener roles de "resource_access"
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

        // Eliminar duplicados y convertir a formato consistente
        return roles.stream()
                .distinct()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());
    }
}
