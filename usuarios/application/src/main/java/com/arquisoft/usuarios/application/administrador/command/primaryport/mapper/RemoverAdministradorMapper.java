package com.arquisoft.usuarios.application.administrador.command.primaryport.mapper;

import com.arquisoft.usuarios.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.usuarios.domain.administrador.RemocionAdministradorDomain;

public final class RemoverAdministradorMapper {

    private RemoverAdministradorMapper() {}

    public static RemocionAdministradorDomain toDomain(RemoverAdministradorCommand command) {
        return RemocionAdministradorDomain.crear(command.usuario(), command.actor());
    }
}
