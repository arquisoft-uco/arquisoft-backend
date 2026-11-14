package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.dto;

import java.util.UUID;

public record PlanRealResponseDTO(
        UUID id,
        UUID tarea,
        int tiempoRealDias,
        String descripcionReal
) {
}
