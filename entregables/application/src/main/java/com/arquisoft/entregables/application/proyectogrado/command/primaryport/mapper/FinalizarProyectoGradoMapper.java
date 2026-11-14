package com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;

public final class FinalizarProyectoGradoMapper {

    private FinalizarProyectoGradoMapper() {}

    public static FinalizacionProyectoGradoDomain toDomain(FinalizarProyectoGradoCommand command) {
        return FinalizacionProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
