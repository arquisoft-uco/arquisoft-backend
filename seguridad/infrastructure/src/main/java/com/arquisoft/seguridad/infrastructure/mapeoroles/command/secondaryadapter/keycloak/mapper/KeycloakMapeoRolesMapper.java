package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.mapper;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeycloakMapeoRolesMapper {

    private static final String CAMPO_NOMBRE = "name";

    private KeycloakMapeoRolesMapper() {}

    public static Set<String> toNombres(List<Map<String, Object>> roles) {
        var nombres = new LinkedHashSet<String>();
        if (UtilObjeto.esNulo(roles)) {
            return nombres;
        }
        for (var rol : roles) {
            if (UtilObjeto.noEsNulo(rol) && rol.get(CAMPO_NOMBRE) instanceof String nombre
                    && !UtilTexto.esVacioONulo(nombre)) {
                nombres.add(nombre);
            }
        }
        return nombres;
    }

    public static MapeoRolesEntity toEntity(Map<String, Set<String>> clientRolesPorRolRealm) {
        return new MapeoRolesEntity(clientRolesPorRolRealm);
    }
}
