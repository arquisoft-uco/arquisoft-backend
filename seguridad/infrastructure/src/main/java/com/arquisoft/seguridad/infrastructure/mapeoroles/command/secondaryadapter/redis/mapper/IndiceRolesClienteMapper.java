package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.mapper;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

public final class IndiceRolesClienteMapper {

    private IndiceRolesClienteMapper() {}

    public static Map<String, Set<String>> toIndice(MapeoRolesEntity mapeo) {
        var indice = new TreeMap<String, Set<String>>();
        mapeo.clientRolesPorRolRealm().forEach((rolRealm, rolesCliente) ->
                rolesCliente.forEach(rolCliente ->
                        indice.computeIfAbsent(rolCliente, clave -> new HashSet<>()).add(rolRealm)));
        return indice;
    }
}
