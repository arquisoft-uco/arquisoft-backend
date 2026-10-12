package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.dto.CambiarEstadoAsesorProyectoGradoRequestDTO;

import java.util.UUID;

public final class CambiarEstadoAsesorProyectoGradoRequestMapper {

    private CambiarEstadoAsesorProyectoGradoRequestMapper() {}

    public static CambiarEstadoAsesorProyectoGradoCommand toCommand(
            CambiarEstadoAsesorProyectoGradoRequestDTO dto, UUID proyectoGrado, UUID asesor,
            UUID coordinador) {
        return CambiarEstadoAsesorProyectoGradoCommand.crear(
                proyectoGrado, asesor, coordinador, dto.estadoAsesor());
    }
}
