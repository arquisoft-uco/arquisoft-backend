package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;

public final class AgregarPlanEstimadoMapper {

    private AgregarPlanEstimadoMapper() {}

    public static PlanEstimadoDomain toDomain(AgregarPlanEstimadoCommand command) {
        return PlanEstimadoDomain.crear(
                command.tarea(), command.tiempoEstimadoDias(), command.descripcionEstimado());
    }
}
