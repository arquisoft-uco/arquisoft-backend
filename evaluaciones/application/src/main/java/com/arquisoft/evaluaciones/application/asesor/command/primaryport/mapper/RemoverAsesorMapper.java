package com.arquisoft.evaluaciones.application.asesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.evaluaciones.domain.asesor.AsesorDomain;

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
