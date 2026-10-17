package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

public final class ConsultarEstadosFichaRequestMapper {

    // Extension realm_access de Keycloak: la emite el proveedor de identidad con estos nombres
    // exactos, son contrato y no texto de catalogo.
    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String CLAVE_ROLES = "roles";

    private static final Map<String, String> ROLES_NEGOCIO_POR_ROL_REALM = Map.of(
            FichasRoles.Realm.ASESOR_FICHA, FichasRoles.Negocio.ASESOR_FICHA,
            FichasRoles.Realm.COORDINADOR, FichasRoles.Negocio.COORDINADOR,
            FichasRoles.Realm.REPRESENTANTE_COMITE, FichasRoles.Negocio.REPRESENTANTE_COMITE);

    private ConsultarEstadosFichaRequestMapper() {}

    public static ConsultarEstadosFichaQuery toQuery(Jwt jwt) {
        var rolesNegocio = extraerRolesRealm(jwt).stream()
                .filter(ROLES_NEGOCIO_POR_ROL_REALM::containsKey)
                .map(ROLES_NEGOCIO_POR_ROL_REALM::get)
                .distinct()
                .toList();

        return ConsultarEstadosFichaQuery.crear(rolesNegocio);
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
