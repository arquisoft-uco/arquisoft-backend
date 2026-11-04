package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AgregarPlanEstimadoRequestDTO;

import java.util.UUID;

public final class AgregarPlanEstimadoRequestMapper {

    private AgregarPlanEstimadoRequestMapper() {}

    public static AgregarPlanEstimadoCommand toCommand(AgregarPlanEstimadoRequestDTO dto, UUID tarea) {
        return AgregarPlanEstimadoCommand.crear(tarea, dto.tiempoEstimadoDias(), dto.descripcionEstimado());
    }
}
