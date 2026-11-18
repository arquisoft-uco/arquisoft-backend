package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto;

import java.util.UUID;

public record PlanEstimadoProyectoResponseDTO(
        UUID id,
        UUID tarea,
        String descripcionTarea,
        UUID mapaRuta,
        int tiempoEstimadoDias,
        String descripcionEstimado
) {
}
