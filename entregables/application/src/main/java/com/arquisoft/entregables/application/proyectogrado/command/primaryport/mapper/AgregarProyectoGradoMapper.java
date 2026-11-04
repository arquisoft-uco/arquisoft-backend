package com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public final class AgregarProyectoGradoMapper {

    private AgregarProyectoGradoMapper() {}

    public static ProyectoGradoDomain toDomain(AgregarProyectoGradoCommand command) {
        return ProyectoGradoDomain.crear(command.proyectoGrado(), command.nombre());
    }
}
