package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.GenerarPaqueteEntregableCommand;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;

public final class GenerarPaqueteEntregableMapper {

    private GenerarPaqueteEntregableMapper() {}

    public static GeneracionPaqueteEntregableDomain toDomain(GenerarPaqueteEntregableCommand command) {
        return GeneracionPaqueteEntregableDomain.crear(
                command.entregableProyectoGrado(), command.proyectoGrado(), command.versionEntregable());
    }
}
