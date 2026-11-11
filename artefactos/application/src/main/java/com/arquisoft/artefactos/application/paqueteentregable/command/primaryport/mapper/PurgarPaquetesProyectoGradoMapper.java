package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;

public final class PurgarPaquetesProyectoGradoMapper {

    private PurgarPaquetesProyectoGradoMapper() {}

    public static PurgaPaquetesProyectoGradoDomain toDomain(PurgarPaquetesProyectoGradoCommand command) {
        return PurgaPaquetesProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
