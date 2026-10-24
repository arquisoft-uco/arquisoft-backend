package com.arquisoft.seguridad.application.mapeoroles.command.result.mapper;

import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;

import java.util.HashSet;

public final class ActualizacionMapeoRolesResultMapper {

    private ActualizacionMapeoRolesResultMapper() {}

    public static ActualizacionMapeoRolesResult.Actualizada toResultActualizada(MapeoRolesEntity mapeo) {
        var rolesCliente = new HashSet<String>();
        mapeo.clientRolesPorRolRealm().values().forEach(rolesCliente::addAll);
        return new ActualizacionMapeoRolesResult.Actualizada(
                mapeo.clientRolesPorRolRealm().size(), rolesCliente.size());
    }

    public static ActualizacionMapeoRolesResult toResultVigente() {
        return new ActualizacionMapeoRolesResult.Vigente();
    }

    public static ActualizacionMapeoRolesResult toResultOmitida() {
        return new ActualizacionMapeoRolesResult.Omitida();
    }

    public static ActualizacionMapeoRolesResult toResultFallida(String motivo) {
        return new ActualizacionMapeoRolesResult.Fallida(motivo);
    }
}
