package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CongelarProyectoGradoCommand;
import com.arquisoft.artefactos.domain.proyectogrado.CongelacionProyectoGradoDomain;

public final class CongelarProyectoGradoMapper {

    private CongelarProyectoGradoMapper() {}

    public static CongelacionProyectoGradoDomain toDomain(CongelarProyectoGradoCommand command) {
        return CongelacionProyectoGradoDomain.crear(command.proyectoGrado(), command.ocurridoEn());
    }
}
