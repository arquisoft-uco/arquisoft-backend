package com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity;

import com.arquisoft.shared.util.UtilObjeto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record MapeoRolesEntity(Map<String, Set<String>> clientRolesPorRolRealm) {

    public MapeoRolesEntity {
        var copia = new HashMap<String, Set<String>>();
        if (UtilObjeto.noEsNulo(clientRolesPorRolRealm)) {
            clientRolesPorRolRealm.forEach((rolRealm, rolesCliente) -> copia.put(rolRealm, Set.copyOf(rolesCliente)));
        }
        clientRolesPorRolRealm = Map.copyOf(copia);
    }
}
