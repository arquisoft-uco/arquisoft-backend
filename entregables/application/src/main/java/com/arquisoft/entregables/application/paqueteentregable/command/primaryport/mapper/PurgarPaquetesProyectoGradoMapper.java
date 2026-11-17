package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;

public final class PurgarPaquetesProyectoGradoMapper {

    private PurgarPaquetesProyectoGradoMapper() {}

    public static PurgaPaquetesProyectoGradoDomain toDomain(PurgarPaquetesProyectoGradoCommand command) {
        return PurgaPaquetesProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
