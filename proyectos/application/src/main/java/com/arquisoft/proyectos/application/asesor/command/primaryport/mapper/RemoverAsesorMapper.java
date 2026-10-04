package com.arquisoft.proyectos.application.asesor.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;

public final class RemoverAsesorMapper {

    private RemoverAsesorMapper() {}

    public static AsesorDomain toDomain(RemoverAsesorCommand command) {
        return AsesorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
