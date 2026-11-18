package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AportarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;

public final class AportarPlanEstimadoMapper {

    private AportarPlanEstimadoMapper() {}

    public static AportePlanEstimadoDomain toDomain(AportarPlanEstimadoCommand command) {
        return AportePlanEstimadoDomain.crear(command.planEstimado(), command.descripcionEstimado());
    }
}
