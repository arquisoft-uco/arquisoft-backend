package com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.model;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;

public sealed interface ObtencionMapeoRoles {

    record Obtenida(MapeoRolesEntity mapeo) implements ObtencionMapeoRoles {}

    record NoDisponible(String motivo) implements ObtencionMapeoRoles {}
}
