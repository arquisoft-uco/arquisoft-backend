package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CambiarEstadoProyectoGradoCommand;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.dto.CambiarEstadoProyectoGradoRequestDTO;

import java.util.UUID;

public final class CambiarEstadoProyectoGradoRequestMapper {

    private CambiarEstadoProyectoGradoRequestMapper() {}

    public static CambiarEstadoProyectoGradoCommand toCommand(
            CambiarEstadoProyectoGradoRequestDTO dto, UUID proyectoGrado, UUID asesor) {
        return CambiarEstadoProyectoGradoCommand.crear(proyectoGrado, asesor, dto.estadoProyecto());
    }
}
