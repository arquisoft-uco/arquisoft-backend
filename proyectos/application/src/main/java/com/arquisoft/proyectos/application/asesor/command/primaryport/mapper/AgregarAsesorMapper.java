package com.arquisoft.proyectos.application.asesor.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;

public final class AgregarAsesorMapper {

    private AgregarAsesorMapper() {}

    public static AsesorDomain toDomain(AgregarAsesorCommand command) {
        return AsesorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
