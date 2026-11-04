package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.AgregarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

public final class AgregarAdministradorMapper {

    private AgregarAdministradorMapper() {}

    public static AdministradorDomain toDomain(AgregarAdministradorCommand command) {
        return AdministradorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
