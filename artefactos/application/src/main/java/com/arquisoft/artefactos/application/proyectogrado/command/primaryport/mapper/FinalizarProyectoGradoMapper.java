package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.artefactos.domain.proyectogrado.FinalizacionProyectoGradoDomain;

public final class FinalizarProyectoGradoMapper {

    private FinalizarProyectoGradoMapper() {}

    public static FinalizacionProyectoGradoDomain toDomain(FinalizarProyectoGradoCommand command) {
        return FinalizacionProyectoGradoDomain.crear(command.proyectoGrado(), command.ocurridoEn());
    }
}
