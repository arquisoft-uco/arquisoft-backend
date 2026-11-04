package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.ModificarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;

public final class ModificarPlanEstimadoMapper {

    private ModificarPlanEstimadoMapper() {}

    public static ModificacionPlanEstimadoDomain toDomain(ModificarPlanEstimadoCommand command) {
        return ModificacionPlanEstimadoDomain.crear(command.planEstimado(), command.descripcionEstimado());
    }
}
