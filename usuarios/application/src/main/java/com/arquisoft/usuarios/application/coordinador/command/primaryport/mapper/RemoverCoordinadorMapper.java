package com.arquisoft.usuarios.application.coordinador.command.primaryport.mapper;

import com.arquisoft.usuarios.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;

public final class RemoverCoordinadorMapper {

    private RemoverCoordinadorMapper() {}

    public static CoordinadorDomain toDomain(RemoverCoordinadorCommand command) {
        return CoordinadorDomain.crear(command.usuario());
    }
}
