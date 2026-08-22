package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt.mapper;

import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

public final class JwtIdentidadMapper {

    // Claims estandar de OIDC mas la extension realm_access de Keycloak: los emite el
    // proveedor de identidad con estos nombres exactos, son contrato y no texto de catalogo.
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NOMBRE = "name";
    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String CLAVE_ROLES = "roles";

    private JwtIdentidadMapper() {}

    public static IdentidadProveedor toModel(Jwt jwt) {
        return new IdentidadProveedor(
                jwt.getSubject(),
                jwt.getClaimAsString(CLAIM_EMAIL),
                jwt.getClaimAsString(CLAIM_NOMBRE),
                extraerRolesRealm(jwt));
    }

    private static List<String> extraerRolesRealm(Jwt jwt) {
        if (jwt.getClaim(CLAIM_REALM_ACCESS) instanceof Map<?, ?> realmAccess
                && realmAccess.get(CLAVE_ROLES) instanceof List<?> rolesCrudos) {
            return rolesCrudos.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }
}
