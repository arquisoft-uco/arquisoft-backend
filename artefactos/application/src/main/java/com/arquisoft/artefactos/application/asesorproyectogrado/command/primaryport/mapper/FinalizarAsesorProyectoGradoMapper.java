package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

public final class FinalizarAsesorProyectoGradoMapper {

    private FinalizarAsesorProyectoGradoMapper() {}

    public static AsesorProyectoGradoDomain toDomain(FinalizarAsesorProyectoGradoCommand command) {
        return AsesorProyectoGradoDomain.crear(command.asesor(), command.proyectoGrado(), command.ocurridoEn());
    }
}
