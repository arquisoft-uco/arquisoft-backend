package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web.dto.CancelarProyectoGradoRequestDTO;

import java.util.UUID;

public final class CancelarProyectoGradoRequestMapper {

    private CancelarProyectoGradoRequestMapper() {}

    public static CancelarProyectoGradoCommand toCommand(
            CancelarProyectoGradoRequestDTO dto, UUID proyectoGrado, UUID coordinador) {
        return CancelarProyectoGradoCommand.crear(proyectoGrado, coordinador, dto.motivo());
    }
}
