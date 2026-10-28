package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.CambiarEstadoEntregableProyectoGradoRequestDTO;

import java.util.UUID;

public final class CambiarEstadoEntregableProyectoGradoRequestMapper {

    private CambiarEstadoEntregableProyectoGradoRequestMapper() {}

    public static CambiarEstadoEntregableProyectoGradoCommand toCommand(
            UUID entregableProyectoGradoId, CambiarEstadoEntregableProyectoGradoRequestDTO dto) {
        return CambiarEstadoEntregableProyectoGradoCommand.crear(entregableProyectoGradoId, dto.estadoEntregable());
    }
}
