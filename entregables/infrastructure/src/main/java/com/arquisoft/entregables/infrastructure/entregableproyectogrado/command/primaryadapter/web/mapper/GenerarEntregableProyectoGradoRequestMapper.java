package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class GenerarEntregableProyectoGradoRequestMapper {

    private GenerarEntregableProyectoGradoRequestMapper() {}

    public static GenerarEntregableProyectoGradoCommand toCommand(UUID proyectoGradoId, IdentidadActual identidad) {
        return GenerarEntregableProyectoGradoCommand.crear(proyectoGradoId, identidad.id());
    }
}
