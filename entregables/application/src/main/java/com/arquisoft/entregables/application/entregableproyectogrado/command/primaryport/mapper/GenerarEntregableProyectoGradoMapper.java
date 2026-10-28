package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;

public final class GenerarEntregableProyectoGradoMapper {

    private GenerarEntregableProyectoGradoMapper() {}

    public static GeneracionEntregableProyectoGradoDomain toDomain(GenerarEntregableProyectoGradoCommand command) {
        return GeneracionEntregableProyectoGradoDomain.crear(command.proyectoGrado(), command.asesor());
    }
}
