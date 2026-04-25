package com.arquisoft.seguridad.infrastructure.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Extrae la lista de roles del claim realm_access.roles del JWT emitido por Keycloak.
 * Usa instanceof pattern matching (Java 16+) para eliminar casts explícitos inseguros.
 */
@Component
public class KeycloakRoleExtractor {

    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String KEY_ROLES = "roles";

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
}
