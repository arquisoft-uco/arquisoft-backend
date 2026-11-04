package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto.PlanEstimadoResponseDTO;

public final class PlanEstimadoResponseMapper {

    private PlanEstimadoResponseMapper() {}

    public static PlanEstimadoResponseDTO toResponse(PlanEstimadoReadModel r) {
        return new PlanEstimadoResponseDTO(
                r.id(),
                r.tarea(),
                r.tiempoEstimadoDias(),
                r.descripcionEstimado());
    }
}
