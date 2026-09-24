package com.arquisoft.proyectos.application.asesor.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;

public final class ActualizarAsesorMapper {

    private ActualizarAsesorMapper() {}

    public static AsesorDomain toDomain(ActualizarAsesorCommand command) {
        return AsesorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
