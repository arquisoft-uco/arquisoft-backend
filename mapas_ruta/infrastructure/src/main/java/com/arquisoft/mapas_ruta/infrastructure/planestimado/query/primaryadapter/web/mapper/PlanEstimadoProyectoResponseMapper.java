package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto.PlanEstimadoProyectoResponseDTO;

public final class PlanEstimadoProyectoResponseMapper {

    private PlanEstimadoProyectoResponseMapper() {}

    public static PlanEstimadoProyectoResponseDTO toResponse(PlanEstimadoProyectoReadModel r) {
        return new PlanEstimadoProyectoResponseDTO(
                r.id(),
                r.tarea(),
                r.descripcionTarea(),
                r.mapaRuta(),
                r.tiempoEstimadoDias(),
                r.descripcionEstimado());
    }
}
