package com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

import java.time.Instant;

public final class AgregacionAdministradorResultMapper {

    private AgregacionAdministradorResultMapper() {}

    public static AgregacionAdministradorResult.Agregada toResultAgregada(AdministradorDomain administrador) {
        return new AgregacionAdministradorResult.Agregada(administrador.getId());
    }

    public static AgregacionAdministradorResult.Reactivada toResultReactivada(AdministradorDomain administrador) {
        return new AgregacionAdministradorResult.Reactivada(administrador.getId());
    }

    public static AgregacionAdministradorResult.Duplicada toResultDuplicada(AdministradorDomain administrador) {
        return new AgregacionAdministradorResult.Duplicada(administrador.getId());
    }

    public static AgregacionAdministradorResult.Descartada toResultDescartada(
            AdministradorDomain administrador, Instant ocurridoEnVigente) {
        return new AgregacionAdministradorResult.Descartada(administrador.getId(), ocurridoEnVigente);
    }
}
