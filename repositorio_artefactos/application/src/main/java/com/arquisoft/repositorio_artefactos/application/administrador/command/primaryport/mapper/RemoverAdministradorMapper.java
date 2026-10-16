package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

public final class RemoverAdministradorMapper {

    private RemoverAdministradorMapper() {}

    public static AdministradorDomain toDomain(RemoverAdministradorCommand command) {
        return AdministradorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
