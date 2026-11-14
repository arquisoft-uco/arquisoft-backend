package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.CambiarEstadoEntregableProyectoGradoRequestDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class CambiarEstadoEntregableProyectoGradoRequestMapper {

    private CambiarEstadoEntregableProyectoGradoRequestMapper() {}

    public static CambiarEstadoEntregableProyectoGradoCommand toCommand(
            UUID entregableProyectoGradoId, CambiarEstadoEntregableProyectoGradoRequestDTO dto,
            IdentidadActual identidad) {
        return CambiarEstadoEntregableProyectoGradoCommand.crear(
                entregableProyectoGradoId, dto.estadoEntregable(), identidad.id());
    }
}
