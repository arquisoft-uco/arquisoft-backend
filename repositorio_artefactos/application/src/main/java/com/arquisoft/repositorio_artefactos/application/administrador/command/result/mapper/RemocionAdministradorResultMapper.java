package com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

import java.time.Instant;

public final class RemocionAdministradorResultMapper {

    private RemocionAdministradorResultMapper() {}

    public static RemocionAdministradorResult.Removida toResultRemovida(AdministradorDomain administrador) {
        return new RemocionAdministradorResult.Removida(administrador.getId());
    }

    public static RemocionAdministradorResult.Lapida toResultLapida(AdministradorDomain administrador) {
        return new RemocionAdministradorResult.Lapida(administrador.getId());
    }

    public static RemocionAdministradorResult.Descartada toResultDescartada(
            AdministradorDomain administrador, Instant ocurridoEnVigente) {
        return new RemocionAdministradorResult.Descartada(administrador.getId(), ocurridoEnVigente);
    }
}
