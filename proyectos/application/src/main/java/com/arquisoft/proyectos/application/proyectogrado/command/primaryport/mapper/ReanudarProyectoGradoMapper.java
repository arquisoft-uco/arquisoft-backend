package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.ReanudarProyectoGradoCommand;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;

public final class ReanudarProyectoGradoMapper {

    private ReanudarProyectoGradoMapper() {}

    public static ReanudacionProyectoGradoDomain toDomain(ReanudarProyectoGradoCommand command) {
        return ReanudacionProyectoGradoDomain.crear(command.proyectoGrado(), command.fechaFin(), command.hoy());
    }
}
