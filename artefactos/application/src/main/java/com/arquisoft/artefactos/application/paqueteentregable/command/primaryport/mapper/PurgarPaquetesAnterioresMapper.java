package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesAnterioresCommand;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;

public final class PurgarPaquetesAnterioresMapper {

    private PurgarPaquetesAnterioresMapper() {}

    public static PurgaPaquetesAnterioresDomain toDomain(PurgarPaquetesAnterioresCommand command) {
        return PurgaPaquetesAnterioresDomain.crear(command.proyectoGrado(), command.versionEntregable());
    }
}
