package com.arquisoft.seguridad.infrastructure.config.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extrae roles de los claims realm_access.roles y resource_access.{clientId}.roles
 * del JWT emitido por Keycloak.
 * Usa instanceof pattern matching (Java 16+) para eliminar casts explícitos inseguros.
 */
@Component
public class KeycloakRoleExtractor {

    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    private static final String KEY_ROLES = "roles";

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    /**
     * Retorna los roles del realm access del token.
     * Retorna lista vacía si el claim no existe o tiene estructura inesperada.
     */
    public List<String> extractRealmRoles(Jwt jwt) {
        if (jwt.getClaim(CLAIM_REALM_ACCESS) instanceof Map<?, ?> realmAccess
                && realmAccess.get(KEY_ROLES) instanceof List<?> rawRoles) {
            return rawRoles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }

    /**
     * Retorna los roles del resource_access.{clientId} del token.
     * Retorna lista vacía si el claim no existe o tiene estructura inesperada.
     */
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

    /**
     * Retorna todos los roles combinados: realm_access + resource_access.
     */
    public List<String> extractAllRoles(Jwt jwt) {
        List<String> all = new ArrayList<>();
        all.addAll(extractRealmRoles(jwt));
        all.addAll(extractResourceRoles(jwt));
        return all;
    }
}
