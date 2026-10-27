package com.arquisoft.artefactos.application.asesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;

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
