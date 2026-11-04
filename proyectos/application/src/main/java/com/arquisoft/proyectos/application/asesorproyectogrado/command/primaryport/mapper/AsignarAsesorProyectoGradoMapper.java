package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;

public final class AsignarAsesorProyectoGradoMapper {

    private AsignarAsesorProyectoGradoMapper() {}

    public static AsignacionAsesorProyectoGradoDomain toDomain(AsignarAsesorProyectoGradoCommand command) {
        var asesorProyectoGrado = AsesorProyectoGradoDomain.crear(command.asesor(), command.proyectoGrado());

        return AsignacionAsesorProyectoGradoDomain.crear(asesorProyectoGrado, command.coordinador());
    }
}
