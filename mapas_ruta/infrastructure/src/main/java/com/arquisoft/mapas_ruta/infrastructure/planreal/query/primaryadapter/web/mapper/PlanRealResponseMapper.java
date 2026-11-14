package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.dto.PlanRealResponseDTO;

public final class PlanRealResponseMapper {

    private PlanRealResponseMapper() {}

    public static PlanRealResponseDTO toResponse(PlanRealReadModel r) {
        return new PlanRealResponseDTO(
                r.id(),
                r.tarea(),
                r.tiempoRealDias(),
                r.descripcionReal());
    }
}
