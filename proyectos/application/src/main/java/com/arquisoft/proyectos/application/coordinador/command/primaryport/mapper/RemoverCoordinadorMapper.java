package com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

public final class RemoverCoordinadorMapper {

    private RemoverCoordinadorMapper() {}

    public static CoordinadorDomain toDomain(RemoverCoordinadorCommand command) {
        return CoordinadorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
