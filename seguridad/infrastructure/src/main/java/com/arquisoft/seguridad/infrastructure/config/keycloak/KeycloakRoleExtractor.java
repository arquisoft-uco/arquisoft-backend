package com.arquisoft.seguridad.infrastructure.config.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class KeycloakRoleExtractor {

    private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    private static final String KEY_ROLES = "roles";

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    public List<String> extractResourceRoles(Jwt jwt) {
        if (jwt.getClaim(CLAIM_RESOURCE_ACCESS) instanceof Map<?, ?> resourceAccess
                && resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess
                && clientAccess.get(KEY_ROLES) instanceof List<?> rawRoles) {
            return rawRoles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }
}
