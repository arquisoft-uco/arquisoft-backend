package com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

import java.time.Instant;

public final class ActualizacionAdministradorResultMapper {

    private ActualizacionAdministradorResultMapper() {}

    public static ActualizacionAdministradorResult.Actualizada toResultActualizada(AdministradorDomain administrador) {
        return new ActualizacionAdministradorResult.Actualizada(administrador.getId());
    }

    public static ActualizacionAdministradorResult.Descartada toResultDescartada(
            AdministradorDomain administrador, Instant ocurridoEnVigente) {
        return new ActualizacionAdministradorResult.Descartada(administrador.getId(), ocurridoEnVigente);
    }

    public static ActualizacionAdministradorResult.NoReplicado toResultNoReplicado(AdministradorDomain administrador) {
        return new ActualizacionAdministradorResult.NoReplicado(administrador.getId());
    }
}
