package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.dto;

import java.util.UUID;

public record PlanEstimadoResponseDTO(
        UUID id,
        UUID tarea,
        int tiempoEstimadoDias,
        String descripcionEstimado
) {
}
