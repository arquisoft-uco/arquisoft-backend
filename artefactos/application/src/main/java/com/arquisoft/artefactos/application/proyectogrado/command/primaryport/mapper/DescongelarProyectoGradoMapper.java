package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.DescongelarProyectoGradoCommand;
import com.arquisoft.artefactos.domain.proyectogrado.DescongelacionProyectoGradoDomain;

public final class DescongelarProyectoGradoMapper {

    private DescongelarProyectoGradoMapper() {}

    public static DescongelacionProyectoGradoDomain toDomain(DescongelarProyectoGradoCommand command) {
        return DescongelacionProyectoGradoDomain.crear(command.proyectoGrado(), command.ocurridoEn());
    }
}
