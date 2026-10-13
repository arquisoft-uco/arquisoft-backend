package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.ActualizarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

public final class ActualizarAdministradorMapper {

    private ActualizarAdministradorMapper() {}

    public static AdministradorDomain toDomain(ActualizarAdministradorCommand command) {
        return AdministradorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
