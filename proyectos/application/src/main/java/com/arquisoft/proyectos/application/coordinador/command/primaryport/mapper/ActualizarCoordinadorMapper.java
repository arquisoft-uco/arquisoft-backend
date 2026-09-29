package com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.ActualizarCoordinadorCommand;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

public final class ActualizarCoordinadorMapper {

    private ActualizarCoordinadorMapper() {}

    public static CoordinadorDomain toDomain(ActualizarCoordinadorCommand command) {
        return CoordinadorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
