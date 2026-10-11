package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;

public final class CambiarEstadoAsesorProyectoGradoMapper {

    private CambiarEstadoAsesorProyectoGradoMapper() {}

    public static CambioEstadoAsesorProyectoGradoDomain toDomain(CambiarEstadoAsesorProyectoGradoCommand command) {
        return CambioEstadoAsesorProyectoGradoDomain.crear(command.proyectoGrado(), command.asesor(),
                command.coordinador(), command.estadoAsesor());
    }
}
