package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.dto.PlanRealProyectoResponseDTO;

public final class PlanRealProyectoResponseMapper {

    private PlanRealProyectoResponseMapper() {}

    public static PlanRealProyectoResponseDTO toResponse(PlanRealProyectoReadModel r) {
        return new PlanRealProyectoResponseDTO(
                r.id(),
                r.tarea(),
                r.descripcionTarea(),
                r.mapaRuta(),
                r.tiempoRealDias(),
                r.descripcionReal());
    }
}
