package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelarProyectoGradoMapper {

    private CancelarProyectoGradoMapper() {}

    public static CancelacionProyectoGradoDomain toDomain(CancelarProyectoGradoCommand command) {
        return CancelacionProyectoGradoDomain.crear(
                command.proyectoGrado(), command.coordinador(), command.motivo());
    }
}
