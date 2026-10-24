package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

public final class AgregarAsesorProyectoGradoMapper {

    private AgregarAsesorProyectoGradoMapper() {}

    public static AsesorProyectoGradoDomain toDomain(AgregarAsesorProyectoGradoCommand command) {
        return AsesorProyectoGradoDomain.crear(command.asesor(), command.proyectoGrado(), command.ocurridoEn());
    }
}
