package com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

public final class FinalizarAsesorProyectoGradoMapper {

    private FinalizarAsesorProyectoGradoMapper() {}

    public static AsesorProyectoGradoDomain toDomain(FinalizarAsesorProyectoGradoCommand command) {
        return AsesorProyectoGradoDomain.crear(command.asesor(), command.proyectoGrado(), command.ocurridoEn());
    }
}
