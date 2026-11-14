package com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelarProyectoGradoMapper {

    private CancelarProyectoGradoMapper() {}

    public static CancelacionProyectoGradoDomain toDomain(CancelarProyectoGradoCommand command) {
        return CancelacionProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
