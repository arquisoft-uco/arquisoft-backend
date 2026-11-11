package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.artefactos.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelarProyectoGradoMapper {

    private CancelarProyectoGradoMapper() {}

    public static CancelacionProyectoGradoDomain toDomain(CancelarProyectoGradoCommand command) {
        return CancelacionProyectoGradoDomain.crear(command.proyectoGrado(), command.ocurridoEn());
    }
}
